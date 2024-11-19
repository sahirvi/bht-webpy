from django.contrib.admin.views.decorators import staff_member_required
from django.contrib.auth.mixins import LoginRequiredMixin
from django.shortcuts import redirect, render
from django.urls import reverse_lazy
from django.views.generic import CreateView, DetailView, ListView
from .forms import ItemsForm, CommentForm, SearchForm, CommentEditForm
from .models import Items, Comment, Vote, Rating
from Shoppingcart.models import ShoppingCart
from django.db.models import Q
from django.http import HttpResponse
from django.template.loader import get_template
from xhtml2pdf import pisa


class ItemListView(ListView):
    model = Items
    context_object_name = 'all_the_items'
    template_name = 'item-list.html'


class ItemDetailView(DetailView):
    model = Items
    context_object_name = 'that_one_item'
    template_name = 'item-detail.html'


class ItemCreateView(LoginRequiredMixin, CreateView):
    login_url = '/useradmin/login/'
    model = Items
    form_class = ItemsForm
    template_name = 'item-create.html'
    success_url = reverse_lazy('item-list')

    def get_context_data(self, **kwargs):
        context = super(ItemCreateView, self).get_context_data(**kwargs)
        can_edit = False
        myuser = self.request.user
        if not myuser.is_anonymous:
            can_edit = myuser.can_edit()
        context['can_edit'] = can_edit
        return context

    def form_valid(self, form):
        form.instance.myuser = self.request.user
        return super().form_valid(form)


def item_list(request):
    context = {'all_the_items': Items.objects.all()}
    return render(request, 'item-list.html', context)


def item_search(request):
    if request.method == 'POST':
        search_string_name = request.POST.get('name')
        items_found = Items.objects.filter(
            Q(description__contains=search_string_name) | Q(name__contains=search_string_name))
        form_in_function_based_view = SearchForm()
        context = {'form': form_in_function_based_view,
                   'items_found': items_found,
                   'show_results': True}
        return render(request, 'item-search.html', context)

    else:
        form_in_function_based_view = SearchForm()
        context = {'form': form_in_function_based_view,
                   'show_results': False}
        return render(request, 'item-search.html', context)


def item_detail(request, **kwargs):
    item_id = kwargs['pk']
    item = Items.objects.get(id=item_id)

    if request.method == 'POST':
        myuser = request.user
        ShoppingCart.add_item(myuser, item)

    if request.method == 'POST':
        form = CommentForm(request.POST)
        form.instance.myuser = request.user
        form.instance.item = item
        if form.is_valid():
            form.save()
        else:
            print(form.errors)

    comments = Comment.objects.filter(item=item)
    context = {'that_one_item': item,
               'comments_for_that_one_item': comments,
               'comment_form': CommentForm}
    return render(request, 'item-detail.html', context)


@staff_member_required(login_url='/useradmin/login/')
def item_delete(request, **kwargs):
    if request.method == 'POST':
        form_in_my_function_based_view = ItemsForm(request.POST)
        form_in_my_function_based_view.instance.user = request.user
        if form_in_my_function_based_view.is_valid():
            form_in_my_function_based_view.save()
        else:
            pass

        return redirect('item-list')

    else:
        can_edit = False
        myuser = request.user
        if not myuser.is_anonymous:
            can_edit = myuser.can_edit()
        item_id = kwargs['pk']
        that_one_item_in_my_function_based_view = Items.objects.get(id=item_id)
        Items.objects.filter(id=item_id).delete()
        context = {'can_edit': can_edit,
                   'that_one_item': that_one_item_in_my_function_based_view,
                   }
        return render(request, 'item-delete.html', context)


def vote(request, pk: str, up_or_down: str):
    try:
        comment = Comment.objects.get(id=int(pk))
        myuser = request.user
        voting = Vote.objects.get(comment_id=comment.id, myuser_id=myuser.id)
    except Vote.DoesNotExist:
        voting = None

    if voting is None:
        comment.vote(myuser, up_or_down)

    else:
        print('You`ve already voted')

    item = comment.item

    comments = Comment.objects.filter(item=item)
    for comment in comments:
        upvotes = comment.get_upvotes_count()
        downvotes = comment.get_downvotes_count()
    context = {'that_one_item': item,
               'upvotes': upvotes,
               'downvotes': downvotes,
               'comments_for_that_one_item': comments,
               'comment_form': CommentForm}
    return render(request, 'item-detail.html', context)


def comment_edit_delete(request, pk: str):
    comment_id = pk
    if request.method == 'POST':
        print('-------------', request.POST)
        if 'edit' in request.POST:
            form = CommentEditForm(request.POST)
            if form.is_valid():
                comment = Comment.objects.get(id=comment_id)
                new_text = form.cleaned_data['text']
                comment.text = new_text
                comment.save()
        elif 'delete' in request.POST:
            Comment.objects.get(id=comment_id).delete()

        return redirect('item-list')

    else:
        can_edit = False
        myuser = request.user
        if not myuser.is_anonymous:
            can_edit = myuser.can_edit()
        comment = Comment.objects.get(id=comment_id)
        form = CommentEditForm(request.POST or None, instance=comment)
        context = {'form': form,
                   'can_edit': can_edit,
                   'comment': comment,
                   }
        return render(request, 'user-comment-edit.html', context)


def item_render_pdf_view(request, *args, **kwargs):
    item_id = kwargs['pk']
    item = Items.objects.get(id=item_id)

    template_path = 'item-pdf.html'
    context = {'that_one_item': item}
    # Create a Django response object, and specify content_type as pdf
    response = HttpResponse(content_type='application/pdf')
    # if download:
    # response['Content-Disposition'] = 'attachment; filename="report.pdf"'
    # if display:
    response['Content-Disposition'] = 'filename="detail.pdf"'
    # find the template and render it.
    template = get_template(template_path)
    html = template.render(context)

    # create a pdf
    pisa_status = pisa.CreatePDF(
        html, dest=response)
    # if error then show some funy view
    if pisa_status.err:
        return HttpResponse('We had some errors <pre>' + html + '</pre>')
    return response


def report_comment(request, pk: str):
    comment_id = pk
    comment = Comment.objects.get(id=comment_id)
    comment.is_reported = True
    comment.save()
    return render(request, 'comment-reported.html')


def rate(request, pk: str, stars: str):
    try:
        item = Items.objects.get(id=int(pk))
        user = request.user
        rating = Rating.objects.get(item_id=item.id, myuser_id=user.id)

    except Rating.DoesNotExist:
        rating = None

    if rating is None:
        item.rate(user, stars)

    return redirect('item-detail', pk=pk)
