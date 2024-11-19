from django.urls import path
from . import views

urlpatterns = [
    path('showcomments/', views.CommentDeleteView.as_view(), name='comment-show'),
    path('editdelete/<int:pk>/', views.comment_edit_delete, name='comment-edit-delete'),
]
