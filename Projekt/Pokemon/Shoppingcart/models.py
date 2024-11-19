from decimal import Decimal
from django.conf import settings
from django.db import models
from django.utils import timezone


class ShoppingCart(models.Model):
    timestamp = models.DateTimeField(default=timezone.now)
    myuser = models.ForeignKey(settings.AUTH_USER_MODEL,
                               on_delete=models.CASCADE,
                               )

    def add_item(myuser, item):
        # Get existing shopping cart, or create a new one if none exists
        shopping_carts = ShoppingCart.objects.filter(myuser=myuser)
        if shopping_carts:
            shopping_cart = shopping_carts.first()
        else:
            shopping_cart = ShoppingCart.objects.create(myuser=myuser)

        product_id = item.id
        product_name = item.name
        price = item.price
        ShoppingCartItem.objects.create(product_id=product_id,
                                        product_name=product_name,
                                        price=price,
                                        quantity=1,
                                        shopping_cart=shopping_cart,
                                        )

    def add_pokemon(myuser, pokemon):
        # Get existing shopping cart, or create a new one if none exists
        shopping_carts = ShoppingCart.objects.filter(myuser=myuser)
        if shopping_carts:
            shopping_cart = shopping_carts.first()
        else:
            shopping_cart = ShoppingCart.objects.create(myuser=myuser)

        # Add pokemon to shopping cart
        product_id = pokemon.id
        product_name = pokemon.name + ' | ' + pokemon.get_type_display() + ' / ' \
                                                                           'Index Nummer: ' + pokemon.number
        price = pokemon.price
        ShoppingCartItem.objects.create(product_id=product_id,
                                        product_name=product_name,
                                        price=price,
                                        quantity=1,
                                        shopping_cart=shopping_cart,
                                        )

    def get_number_of_items(self):
        shopping_cart_items = ShoppingCartItem.objects.filter(shopping_cart=self)
        return len(shopping_cart_items)

    def get_total(self):
        total = Decimal(0.0)  # Default without Decimal() would be type float!
        shopping_cart_items = ShoppingCartItem.objects.filter(shopping_cart=self)
        for item in shopping_cart_items:
            total += item.price * item.quantity
        return total


class ShoppingCartItem(models.Model):
    product_id = models.IntegerField()
    product_name = models.CharField(max_length=100)
    price = models.DecimalField(decimal_places=2, max_digits=10)
    quantity = models.IntegerField(default=1)
    shopping_cart = models.ForeignKey(ShoppingCart,
                                      on_delete=models.CASCADE,
                                      )


class Payment(models.Model):
    credit_card_number = models.CharField(max_length=19)  # Format: 1234 5678 1234 5678
    expiry_date = models.CharField(max_length=7)  # Format: 10/2022
    amount = models.DecimalField(decimal_places=2, max_digits=10)
    timestamp = models.DateTimeField(default=timezone.now)
    myuser = models.ForeignKey(settings.AUTH_USER_MODEL,
                               on_delete=models.CASCADE,
                               )
