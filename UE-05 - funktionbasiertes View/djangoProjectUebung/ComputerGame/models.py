from datetime import date

from django.db import models
from django.contrib.auth.models import User


class Games(models.Model):
    GAME_TYPES = [
        ("sp", "sports"),
        ("sim", "simulation"),
        ("fps", "first person shooters"),
        ("rpg", "role playing"),
        ("pg", "party games"),
        ("pzz", "puzzle games"),
        ("rts", "realtime strategy"),
        ("mba", "multiplayer online battle arena"),
        ("aa", "action adventures"),
    ]

    USK = [
        (0, 0),
        (6, 6),
        (12, 12),
        (16, 16),
        (18, 18)
    ]

    name = models.CharField(max_length=100)
    descriptionText = models.CharField(max_length=200)
    genre = models.CharField(max_length=3, choices=GAME_TYPES)
    USK = models.IntegerField(choices=USK)
    creator = models.CharField(max_length=100)
    datePublished = models.DateTimeField(blank=True, default=date.today, )
    user = models.ForeignKey(User,
                             on_delete=models.CASCADE,
                             related_name="users",
                             related_query_name="user"
                             )

    class Meta:
        ordering = ["name", "-genre"]
        verbose_name = "Game"
        verbose_name_plural = "Games"
