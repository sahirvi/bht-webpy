# Generated by Django 3.2.2 on 2021-05-06 15:08

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('ComputerGame', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='games',
            old_name='createDate',
            new_name='datePublished',
        ),
        migrations.AlterField(
            model_name='games',
            name='FSK',
            field=models.IntegerField(choices=[(0, 0), (6, 6), (12, 12), (16, 16), (18, 18)]),
        ),
        migrations.AlterField(
            model_name='games',
            name='genre',
            field=models.CharField(choices=[('sp', 'sports'), ('sim', 'simulation'), ('fps', 'first person shooters'), ('rpg', 'role playing'), ('pg', 'party games'), ('pzz', 'puzzle games'), ('rts', 'realtime strategy'), ('mba', 'multiplayer online battle arena'), ('aa', 'action adventures')], max_length=3),
        ),
    ]