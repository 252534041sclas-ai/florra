from django.db import models
from django.contrib.auth.models import AbstractBaseUser, BaseUserManager

class CustomerUserManager(BaseUserManager):
    def create_user(self, email, password=None, full_name="", mobile=""):
        if not email:
            raise ValueError("Email required")

        user = self.model(
            email=self.normalize_email(email),
            full_name=full_name,
            mobile=mobile
        )
        user.set_password(password)
        user.save(using=self._db)
        return user


class CustomerUser(AbstractBaseUser):
    email = models.EmailField(unique=True)
    full_name = models.CharField(max_length=150)
    mobile = models.CharField(max_length=15, blank=True)
    profile_image = models.ImageField(upload_to="profile_images/", null=True, blank=True)

    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    objects = CustomerUserManager()

    USERNAME_FIELD = "email"

    def __str__(self):
        return self.email


import uuid

class CustomerToken(models.Model):
    user = models.OneToOneField(CustomerUser, on_delete=models.CASCADE)
    key = models.CharField(max_length=40, unique=True, default=uuid.uuid4)

    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.user.email


class VerificationOTP(models.Model):
    PURPOSE_CHOICES = (
        ('register', 'Register'),
        ('reset', 'Reset Password'),
    )

    email = models.EmailField()
    otp = models.CharField(max_length=6)
    purpose = models.CharField(max_length=10, choices=PURPOSE_CHOICES)
    created_at = models.DateTimeField(auto_now_add=True)
    is_verified = models.BooleanField(default=False)

    def __str__(self):
        return f"{self.email} - {self.otp} ({self.purpose})"



from django.db import models

class Quotation(models.Model):

    STATUS_CHOICES = (
        ("PENDING", "Pending"),
        ("APPROVED", "Approved"),
        ("REJECTED", "Rejected"),
    )

    quotation_id = models.CharField(max_length=20, unique=True)

    # Customer details
    customer_name = models.CharField(max_length=100)
    phone = models.CharField(max_length=20)
    email = models.EmailField()

    # Product details
    product_name = models.CharField(max_length=200)
    product_details = models.CharField(max_length=200)
    stock_status = models.CharField(max_length=20)

    # Project info
    project_type = models.CharField(max_length=20)
    room_type = models.CharField(max_length=50)
    total_area = models.CharField(max_length=50)
    additional_notes = models.TextField(blank=True)

    # Admin reply
    admin_reply = models.TextField(blank=True)
    total_amount = models.DecimalField(
        max_digits=10, decimal_places=2, null=True, blank=True
    )

    status = models.CharField(
        max_length=20, choices=STATUS_CHOICES, default="PENDING"
    )

    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.quotation_id


# florra/models.py

from django.db import models
from django.contrib.auth import get_user_model

User = get_user_model()

from florra_admin.models import Product

class Favorite(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    product = models.ForeignKey(Product, on_delete=models.CASCADE)

    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ("user", "product")  # duplicate prevent

    def __str__(self):
        return f"{self.user} → {self.product.tile_name}"


from django.conf import settings
from django.db import models

User = settings.AUTH_USER_MODEL


class Notification(models.Model):

    TYPE_CHOICES = (
        ("quotation", "Quotation"),
        ("system", "System"),
        ("promotion", "Promotion"),
        ("alert", "Alert"),
        ("announcement", "Announcement"),
    )

    user = models.ForeignKey(User, on_delete=models.CASCADE)
    title = models.CharField(max_length=255)
    message = models.TextField()

    notification_type = models.CharField(
        max_length=20,
        choices=TYPE_CHOICES
    )

    is_read = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user} - {self.title}"


