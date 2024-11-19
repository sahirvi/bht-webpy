from django import forms
from .models import Items, Comment


class ItemsForm(forms.ModelForm):
    class Meta:
        model = Items
        fields = ['name', 'product_picture', 'description', 'price']
        widgets = {
            'user': forms.HiddenInput(),
        }


class CommentForm(forms.ModelForm):
    class Meta:
        model = Comment
        fields = ['text']
        widgets = {
            'user': forms.HiddenInput(),
            'item': forms.HiddenInput(),
        }


class SearchForm(forms.ModelForm):
    name = forms.CharField(required=False)

    class Meta:
        model = Items
        fields = ['name']


class CommentEditForm(forms.ModelForm):

    class Meta:
        model = Comment
        fields = ['text']
        widgets = {
            'comment_id': forms.HiddenInput(),
        }
