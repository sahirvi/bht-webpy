# Generated by Django 3.2.2 on 2021-05-10 07:10

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('ComputerGame', '0003_rename_fsk_games_usk'),
    ]

    operations = [
        migrations.AlterField(
            model_name='games',
            name='USK',
            field=models.IntegerField(choices=[(0, 0), (6, 6), (12, 12), (16, 16), (18, 18)], max_length=2),
        ),
    ]
