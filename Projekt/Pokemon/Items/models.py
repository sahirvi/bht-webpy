from django.db import models
from django.conf import settings


class Items(models.Model):
    name = models.CharField(max_length=100)
    product_picture = models.ImageField(upload_to='product_pictures/', blank=True, null=True)
    description = models.CharField(max_length=200)
    price = models.DecimalField(decimal_places=2, max_digits=10)
    myuser = models.ForeignKey(settings.AUTH_USER_MODEL,
                               on_delete=models.CASCADE,
                               related_name='item_created_by',
                               related_query_name='item_created_by'
                               )

    class Meta:
        ordering = ['name', '-price']
        verbose_name = 'Item'
        verbose_name_plural = 'Customerservice'

    def get_first_star(self):
        get_first = Rating.objects.filter(rate='1',
                                          item=self)
        return get_first

    def get_second_star(self):
        get_second = Rating.objects.filter(rate='2',
                                           item=self)
        return get_second

    def get_third_star(self):
        get_third = Rating.objects.filter(rate='2',
                                          item=self)
        return get_third

    def get_fourth_star(self):
        get_fourth = Rating.objects.filter(rate='2',
                                           item=self)
        return get_fourth

    def get_fifth_star(self):
        get_fifth = Rating.objects.filter(rate='2',
                                          item=self)
        return get_fifth

    def rate(self, myuser, rate):
        stars = '1'
        if rate == '2':
            stars = '2'
        if rate == '3':
            stars = '3'
        if rate == '4':
            stars = '4'
        if rate == '5':
            stars = '5'

        rate = Rating.objects.create(rate=stars,
                                     myuser=myuser,
                                     item=self
                                     )


class Comment(models.Model):
    text = models.TextField(max_length=500)
    timestamp = models.DateTimeField(auto_now_add=True)
    myuser = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    item = models.ForeignKey(Items, on_delete=models.CASCADE)
    is_reported = models.BooleanField(default=False)

    class Meta:
        ordering = ['timestamp']
        verbose_name = 'Comment'
        verbose_name_plural = 'Comments'

    def get_comment_prefix(self):
        if len(self.text) > 50:
            return self.text[:50] + '...'
        else:
            return self.text

    def __str__(self):
        return self.get_comment_prefix() + ' (' + self.myuser.username + ')'

    def __repr__(self):
        return self.get_comment_prefix() + ' (' + self.myuser.username + ' / ' + str(self.timestamp) + ')'

    def get_upvotes(self):
        upvotes = Vote.objects.filter(up_or_down='U',
                                      comment=self)
        return upvotes

    def get_upvotes_count(self):
        return len(self.get_upvotes())

    def get_downvotes(self):
        downvotes = Vote.objects.filter(up_or_down='D',
                                        comment=self)
        return downvotes

    def get_downvotes_count(self):
        return len(self.get_downvotes())

    def vote(self, myuser, up_or_down):
        U_or_D = 'U'
        if up_or_down == 'down':
            U_or_D = 'D'
        vote = Vote.objects.create(up_or_down=U_or_D,
                                   myuser=myuser,
                                   comment=self
                                   )

    def rate(self, myuser, rate):
        U_or_D = 'U'
        if rate == 'down':
            U_or_D = 'D'
        vote = Vote.objects.create(up_or_down=U_or_D,
                                   myuser=myuser,
                                   comment=self
                                   )


class Rating(models.Model):
    RATING_TYPES = [
        ('1', 'bad'),
        ('2', 'okay'),
        ('3', 'average'),
        ('4', 'good'),
        ('5', 'super good'),
    ]

    rate = models.CharField(max_length=2, choices=RATING_TYPES)
    item = models.ForeignKey(Items, on_delete=models.CASCADE)
    myuser = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)


class Vote(models.Model):
    VOTE_TYPES = [
        ('U', 'up'),
        ('D', 'down'),
    ]

    up_or_down = models.CharField(max_length=1,
                                  choices=VOTE_TYPES,
                                  )
    timestamp = models.DateTimeField(auto_now_add=True)
    myuser = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE,
                               related_name='votes_in_comments_created_by',
                               related_query_name='votes_in_comments_created_by', )
    comment = models.ForeignKey(Comment, on_delete=models.CASCADE)
