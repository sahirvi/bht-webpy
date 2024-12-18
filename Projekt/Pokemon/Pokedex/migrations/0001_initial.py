# Generated by Django 3.2.4 on 2021-07-15 11:13

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Pokemon',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100)),
                ('product_picture', models.ImageField(blank=True, null=True, upload_to='product_pictures/')),
                ('type', models.CharField(choices=[('f', 'fire'), ('w', 'water'), ('g', 'ground'), ('a', 'air'), ('gr', 'grass'), ('gh', 'ghost')], max_length=2)),
                ('number', models.IntegerField()),
                ('hp', models.IntegerField()),
                ('ap', models.IntegerField()),
                ('defense', models.IntegerField()),
                ('price', models.DecimalField(decimal_places=2, max_digits=10)),
                ('some_file', models.FileField(blank=True, null=True, upload_to='uploaded_files/')),
            ],
            options={
                'verbose_name': 'Pokemon',
                'verbose_name_plural': 'Pokemons',
                'ordering': ['name', '-number'],
            },
        ),
        migrations.CreateModel(
            name='Vote',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('up_or_down', models.CharField(choices=[('U', 'up'), ('D', 'down')], max_length=1)),
                ('timestamp', models.DateTimeField(auto_now_add=True)),
            ],
        ),
    ]
