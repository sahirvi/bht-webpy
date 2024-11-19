from django.contrib.admin.views.decorators import staff_member_required
from django.contrib.auth.mixins import LoginRequiredMixin
from django.shortcuts import redirect, render
from django.urls import reverse_lazy
from django.views.generic import CreateView, DetailView, ListView
from .forms import PokemonForm, SearchForm
from .models import Pokemon
from Shoppingcart.models import ShoppingCart
from django.http import HttpResponse
from django.template.loader import get_template
from xhtml2pdf import pisa


class PokemonListView(ListView):
    model = Pokemon
    context_object_name = 'all_the_pokemon'
    template_name = 'pokemon-list.html'


class PokemonDetailView(DetailView):
    model = Pokemon
    context_object_name = 'that_one_pokemon'
    template_name = 'pokemon-detail.html'


class PokemonCreateView(LoginRequiredMixin, CreateView):
    login_url = '/useradmin/login/'
    model = Pokemon
    form_class = PokemonForm
    template_name = 'pokemon-create.html'
    success_url = reverse_lazy('pokemon-list')

    def get_context_data(self, **kwargs):
        context = super(PokemonCreateView, self).get_context_data(**kwargs)
        can_edit = False
        myuser = self.request.user
        if not myuser.is_anonymous:
            can_edit = myuser.can_edit()
        context['can_edit'] = can_edit
        return context

    def form_valid(self, form):
        form.instance.user = self.request.user
        print('something')
        return super().form_valid(form)


def pokemon_list(request):
    context = {'all_the_pokemons': Pokemon.objects.all()}
    return render(request, 'pokemon-list.html', context)


def pokemon_search(request):
    if request.method == 'POST':
        search_string_name = request.POST['name']
        pokemons_found = Pokemon.objects.filter(name__contains=search_string_name)

        search_string_type = request.POST["type"]
        if search_string_type:
            pokemons_found = pokemons_found.filter(type__contains=search_string_type)

        form_in_function_based_view = SearchForm()
        context = {'form': form_in_function_based_view,
                   'pokemons_found': pokemons_found,
                   'show_results': True}
        return render(request, 'pokemon-search.html', context)

    else:
        form_in_function_based_view = SearchForm()
        context = {'form': form_in_function_based_view,
                   'show_results': False}
        return render(request, 'pokemon-search.html', context)


def pokemon_detail(request, **kwargs):
    pokemon_id = kwargs['pk']
    pokemon = Pokemon.objects.get(id=pokemon_id)

    if request.method == 'POST':
        myuser = request.user
        ShoppingCart.add_item(myuser, pokemon)

    context = {'that_one_pokemon': pokemon, }
    return render(request, 'pokemon-detail.html', context)


@staff_member_required(login_url='/useradmin/login/')
def pokemon_delete(request, **kwargs):
    if request.method == 'POST':
        form_in_my_function_based_view = PokemonForm(request.POST)
        form_in_my_function_based_view.instance.user = request.user
        if form_in_my_function_based_view.is_valid():
            form_in_my_function_based_view.save()
        else:
            pass

        return redirect('pokemon-list')

    else:
        can_edit = False
        myuser = request.user
        if not myuser.is_anonymous:
            can_edit = myuser.can_edit()
        pokemons_id = kwargs['pk']
        that_one_pokemon_in_my_function_based_view = Pokemon.objects.get(id=pokemons_id)
        Pokemon.objects.filter(id=pokemons_id).delete()
        context = {'can_edit': can_edit,
                   'that_one_pokemon': that_one_pokemon_in_my_function_based_view,
                   }
        return render(request, 'pokemon-delete.html', context)


def pokemon_render_pdf_view(request, *args, **kwargs):
    pokemon_id = kwargs['pk']
    pokemon = Pokemon.objects.get(id=pokemon_id)

    template_path = 'pokemon-pdf.html'
    context = {'that_one_pokemon': pokemon}
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
