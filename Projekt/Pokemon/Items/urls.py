from . import views
from django.urls import path

urlpatterns = [
    path('show/', views.ItemListView.as_view(), name='item-list'),
    path('add/', views.ItemCreateView.as_view(), name='item-create'),
    path('search/', views.item_search, name='item-search'),
    path('show/<int:pk>/', views.item_detail, name='item-detail'),
    path('show/<int:pk>/<str:up_or_down>/', views.vote, name='comment-vote'),
    path('delete/<int:pk>', views.item_delete, name='item-delete'),
    path('show/<int:pk>/rate/<str:stars>/', views.rate, name='item-rate'),
    path('comment-edit/<int:pk>/', views.comment_edit_delete, name='user-comment-edit'),
    path('pdf/<int:pk>/', views.item_render_pdf_view, name='item-pdf'),
    path('report/<int:pk>', views.report_comment, name='comment-report'),
]
