# Generated by Django 3.2.4 on 2021-07-15 20:07

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('Pokedex', '0003_alter_pokemon_options'),
    ]

    operations = [
        migrations.DeleteModel(
            name='Vote',
        ),
    ]
