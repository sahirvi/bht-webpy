from django import forms
from .models import Pokemon


class PokemonForm(forms.ModelForm):
    class Meta:
        model = Pokemon
        fields = ['name', 'product_picture', 'type', 'number', 'hp', 'ap', 'defense', 'price', 'some_file']
        widgets = {
            'type': forms.Select(choices=Pokemon.POKEMON_TYPES),
            'user': forms.HiddenInput(),
        }


class SearchForm(forms.ModelForm):
    name = forms.CharField(required=False)

    class Meta:
        model = Pokemon
        fields = ['name', "type"]
