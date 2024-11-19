from django.contrib.admin.views.decorators import staff_member_required
from django.contrib.auth.mixins import LoginRequiredMixin
from django.shortcuts import redirect, render
from django.urls import reverse_lazy
from django.views.generic import ListView, UpdateView
from .forms import CommentEditForm
from Items.models import Comment


class CommentDeleteView(LoginRequiredMixin, ListView):
    login_url = '/useradmin/login/'
    model = Comment
    context_object_name = 'all_the_comments'
    template_name = 'comment-show.html'

    def get_context_data(self, **kwargs):
        context = super(CommentDeleteView, self).get_context_data(**kwargs)
        can_edit = False
        myuser = self.request.user
        if not myuser.is_anonymous:
            can_edit = myuser.can_edit()
        context['can_edit'] = can_edit
        return context

    def post(self, request, *args, **kwargs):
        comment_id = request.POST['comment_id']
        if 'delete' in request.POST:
            Comment.objects.get(id=comment_id).delete()
            return redirect('comment-show')


@staff_member_required(login_url='/useradmin/login/')
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

        return redirect('comment-show')

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
        return render(request, 'comment-edit-delete.html', context)
