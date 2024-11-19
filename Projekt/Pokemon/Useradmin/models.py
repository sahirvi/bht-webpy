from datetime import date, datetime
from django.contrib.auth.models import AbstractUser
from django.db import models
from Shoppingcart.models import ShoppingCart


def get_date_20_years_ago():
    now = datetime.now()
    year = now.year
    month = now.month
    day = now.day
    return date(year - 20, month, day)


class MyUser(AbstractUser):
    USER_TYPES = [
        ('SU', 'superuser'),
        ('CS', 'customer service'),
        ('CU', 'customer'),
    ]

    date_of_birth = models.DateField(default=get_date_20_years_ago())  # Default is 20 years old
    profile_picture = models.ImageField(upload_to='profile_pictures/', blank=True, null=True)
    some_file = models.FileField(upload_to='uploaded_files/', blank=True, null=True)
    type = models.CharField(max_length=2,
                            choices=USER_TYPES,
                            default='CU',
                            )
    favourite_pokemon = models.CharField(max_length=100)


    def can_edit(self):
        return self.is_superuser_or_customer_service()
    # return self.is_superuser_or_staff()

    def count_shopping_cart_items(self):
        count = 0
        if self.is_authenticated:
            shopping_carts = ShoppingCart.objects.filter(myuser=self)
            if shopping_carts:
                shopping_cart = shopping_carts.first()
                count = shopping_cart.get_number_of_items()

        return count

    def has_birthday_today(self):
        return_boolean = False

        now = datetime.now()
        today_month = now.month
        today_day = now.day

        user_month = self.date_of_birth.month
        user_day = self.date_of_birth.day

        if user_month == today_month and user_day == today_day:
            return_boolean = True
        return return_boolean

    def is_superuser_or_customer_service(self):
        if self.type == 'SU' or self.type == 'CS':
            return True
        else:
            return False

    def is_superuser_or_staff(self):
        return self.is_superuser or self.is_staff

    def __str__(self):
        return self.first_name + ' ' + self.last_name + ' (' + str(self.date_of_birth) + ')'
