from django.urls import reverse_lazy
from django.views.generic import CreateView, DetailView, ListView, DeleteView
from .forms import GameForm
from .models import Games


class GameListView(ListView):
    model = Games
    context_object_name = "all_the_games"
    template_name = "game-list.html"


class GameDetailView(DetailView):
    model = Games
    context_object_name = 'that_one_game'  # Default: game
    template_name = 'game-detail.html'


class GameCreateView(CreateView):
    model = Games
    form_class = GameForm
    template_name = 'game-create.html'
    success_url = reverse_lazy('game-list')

    def form_valid(self, form):
        form.instance.user = self.request.user
        return super().form_valid(form)


class GameDeleteView(DeleteView):
    model = Games
    template_name = 'game-delete.html'
    context_object_name = 'that_one_game'
    success_url = reverse_lazy('game-list')
