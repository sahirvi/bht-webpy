from django import forms

from .models import Games


class GameForm(forms.ModelForm):

    class Meta:
        model = Games
        fields = ['name', 'descriptionText', 'genre', 'USK', 'creator', 'datePublished']
        widgets = {
            'genre': forms.Select(choices=Games.GAME_TYPES),
            'user': forms.HiddenInput(),
        }
