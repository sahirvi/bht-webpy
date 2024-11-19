from django.db import models
from django.conf import settings


class Pokemon(models.Model):
    POKEMON_TYPES = [
        ("f", "fire"),
        ("w", "water"),
        ("g", "ground"),
        ("a", "air"),
        ("gr", "grass"),
        ("gh", "ghost"),
    ]

    name = models.CharField(max_length=100)
    product_picture = models.ImageField(upload_to='product_pictures/', blank=True, null=True)
    type = models.CharField(max_length=2, choices=POKEMON_TYPES)
    number = models.IntegerField()
    hp = models.IntegerField()
    ap = models.IntegerField()
    defense = models.IntegerField()
    price = models.DecimalField(decimal_places=2, max_digits=10)
    some_file = models.FileField(upload_to='uploaded_files/', blank=True, null=True)
    myuser = models.ForeignKey(settings.AUTH_USER_MODEL,
                               default=1,
                               on_delete=models.CASCADE,
                               related_name='pokemon_created_by',
                               related_query_name='pokemon_created_by'
                               )

    class Meta:
        ordering = ['number', '-name']
        verbose_name = 'Pokemon'
        verbose_name_plural = 'Pokemons'
