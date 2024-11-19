from django.urls import path
from . import views

urlpatterns = [
    path('show/', views.game_list, name='pokemon-list'),
    path('show/<int:pk>/', views.game_detail, name='pokemon-detail'),
    path('add/', views.game_create, name='pokemon-create'),
    path('delete/<int:pk>', views.game_delete, name='pokemon-delete'),
]
