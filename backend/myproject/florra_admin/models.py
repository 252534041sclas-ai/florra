from django.db import models
from django.utils.crypto import get_random_string
from django.contrib.auth.hashers import make_password, check_password


class AdminUser(models.Model):
    full_name = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    password = models.CharField(max_length=128)
    is_active = models.BooleanField(default=True)
    role = models.CharField(max_length=20, choices=[('admin', 'Admin'), ('staff', 'Staff')], default='admin')
    can_access_billing = models.BooleanField(default=False)
    can_access_reports = models.BooleanField(default=False)
    can_access_predictions = models.BooleanField(default=False)
    mobile = models.CharField(max_length=15, blank=True, null=True)
    profile_image = models.ImageField(upload_to="profile_images/", null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def set_password(self, raw_password):
        self.password = make_password(raw_password)

    def check_password(self, raw_password):
        return check_password(raw_password, self.password)

    @property
    def is_authenticated(self):
        return True

    @property
    def is_anonymous(self):
        return False

    def __str__(self):
        return self.email


class AdminToken(models.Model):
    admin = models.OneToOneField(
        AdminUser,
        on_delete=models.CASCADE,
        related_name="token"
    )
    key = models.CharField(max_length=40, unique=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def save(self, *args, **kwargs):
        if not self.key:
            self.key = get_random_string(40)
        super().save(*args, **kwargs)

    def __str__(self):
        return self.key


from django.db import models

class Product(models.Model):
    CATEGORY_CHOICES = [
        ('Living', 'Living'),
        ('Bathroom', 'Bathroom'),
        ('Bedroom', 'Bedroom'),
        ('Kitchen', 'Kitchen'),
        ('Wall', 'Wall'),
        ('Floor', 'Floor'),
        ('Parking', 'Parking'),
        ('Steps', 'Steps'),
        ('Roof', 'Roof'),
        # Keeping original materials as they might be useful or legacy
        ('Ceramic', 'Ceramic'),
        ('Porcelain', 'Porcelain'),
        ('Vitrified', 'Vitrified'),
        ('Mosaic', 'Mosaic'),
        ('Natural Stone', 'Natural Stone'),
        ('Glass', 'Glass'),
    ]

    FINISH_CHOICES = [
        ('Glossy', 'Glossy'),
        ('Matte', 'Matte'),
        ('Satin', 'Satin'),
        ('Rustic', 'Rustic'),
    ]

    tile_name = models.CharField(max_length=200)
    tile_no = models.CharField(max_length=50, blank=True, null=True) # Added tile_no
    brand_name = models.CharField(max_length=100, blank=True)
    category = models.CharField(max_length=50, choices=CATEGORY_CHOICES)
    size = models.CharField(max_length=50)
    finish = models.CharField(max_length=50, choices=FINISH_CHOICES)
    color = models.CharField(max_length=50)

    price = models.DecimalField(max_digits=10, decimal_places=2)
    cost_price = models.DecimalField(max_digits=10, decimal_places=2, default=0.00)
    stock = models.IntegerField()

    description = models.TextField(blank=True)
    is_active = models.BooleanField(default=True)

    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    image = models.ImageField(upload_to="products/", null=True, blank=True)


    @property
    def thickness(self):
        cat = str(self.category).lower()
        if 'parking' in cat:
            return "15 mm"
        elif 'steps' in cat or 'floor' in cat:
            return "12 mm"
        elif 'roof' in cat:
            return "10 mm"
        else:
            return "9 mm"

    @property
    def coverage(self):
        sz = str(self.size).lower()
        if '80x80' in sz:
            return "1.92 Sq. Mt (20.67 Sq. Ft) per Box"
        elif '60x120' in sz or '20x120' in sz:
            return "1.44 Sq. Mt (15.50 Sq. Ft) per Box"
        elif '60x60' in sz or '30x60' in sz:
            return "1.44 Sq. Mt (15.50 Sq. Ft) per Box"
        elif '40x40' in sz:
            return "0.96 Sq. Mt (10.33 Sq. Ft) per Box"
        else:
            return "1.44 Sq. Mt (15.50 Sq. Ft) per Box"

    def __str__(self):
        return self.tile_name


from django.db import models


class Bill(models.Model):
    STATUS_CHOICES = [
        ('Paid', 'Paid'),
        ('Unpaid', 'Unpaid'),
        ('Cancelled', 'Cancelled'),
    ]

    bill_no = models.CharField(max_length=50, unique=True)
    bill_date = models.DateTimeField(auto_now_add=True)

    customer_name = models.CharField(max_length=100)
    customer_phone = models.CharField(max_length=20)
    customer_address = models.TextField()

    subtotal = models.DecimalField(max_digits=10, decimal_places=2)
    gst_percentage = models.DecimalField(max_digits=5, decimal_places=2)
    gst_amount = models.DecimalField(max_digits=10, decimal_places=2)
    discount = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    loading = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    grand_total = models.DecimalField(max_digits=10, decimal_places=2)

    status = models.CharField(
        max_length=20,
        choices=STATUS_CHOICES,
        default='Unpaid'
    )

    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.bill_no


class BillItem(models.Model):
    bill = models.ForeignKey(
        Bill,
        related_name='items',
        on_delete=models.CASCADE
    )

    item_name = models.CharField(max_length=200)
    size = models.CharField(max_length=100)
    quantity = models.IntegerField()
    rate = models.DecimalField(max_digits=10, decimal_places=2)
    amount = models.DecimalField(max_digits=10, decimal_places=2)

    def __str__(self):
        return self.item_name


from django.db import models

class Enquiry(models.Model):

    STATUS_CHOICES = (
        ('new', 'New'),
        ('quoted', 'Quoted'),
        ('follow_up', 'Follow-up'),
        ('site_visit', 'Site Visit'),
        ('resolved', 'Resolved'),
    )

    customer_name = models.CharField(max_length=100)
    phone = models.CharField(max_length=20)
    message = models.TextField()

    status = models.CharField(
        max_length=20,
        choices=STATUS_CHOICES,
        default='new'
    )

    reference = models.CharField(
        max_length=50,
        blank=True,
        null=True
    )  # quote no / order no / visit time

    # Quotation Details
    quotation_price = models.CharField(max_length=50, blank=True, null=True)
    quotation_boxes = models.CharField(max_length=50, blank=True, null=True)
    quotation_delivery_time = models.CharField(max_length=50, blank=True, null=True)
    quotation_notes = models.TextField(blank=True, null=True)
    admin_name = models.CharField(max_length=100, blank=True, null=True)

    # Link to Registered Customer
    customer_email = models.EmailField(blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True)


    def __str__(self):
        return f"{self.customer_name} - {self.status}"


class AdminNotification(models.Model):
    TYPE_CHOICES = [
        ('system', 'System'),
        ('promotion', 'Promotion'),
        ('alert', 'Alert'),
        ('announcement', 'Announcement'),
    ]

    title = models.CharField(max_length=200)
    message = models.TextField()
    notification_type = models.CharField(
        max_length=20,
        choices=TYPE_CHOICES,
        default='system'
    )
    created_at = models.DateTimeField(auto_now_add=True)
    sent_by = models.CharField(max_length=100, default='Admin')

    def __str__(self):
        return f"[{self.notification_type.upper()}] {self.title}"




