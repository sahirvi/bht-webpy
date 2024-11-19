from . import views
from django.urls import path

urlpatterns = [
    path('show/', views.PokemonListView.as_view(), name='pokemon-list'),
    path('add/', views.PokemonCreateView.as_view(), name='pokemon-create'),
    path('search/', views.pokemon_search, name='pokemon-search'),
    path('show/<int:pk>/', views.pokemon_detail, name='pokemon-detail'),
    path('delete/<int:pk>', views.pokemon_delete, name='pokemon-delete'),
    path('pdf/<int:pk>/', views.pokemon_render_pdf_view, name='pokemon-pdf'),
]
