from django.contrib.auth.forms import UserCreationForm
from .models import MyUser
from django import forms


class MySignUpForm(UserCreationForm):
    class Meta:
        model = MyUser
        fields = ('username', 'first_name', 'last_name', 'email', 'date_of_birth', 'profile_picture', 'some_file',
                  'favourite_pokemon')
        widgets = {
            'profile_picture': forms.FileInput(attrs={"class": "btn btn-primary"}),
            'some_file': forms.FileInput(attrs={"class": "btn btn-primary"})}
    # password ist wegen UserCreationForm schon mit dabei
