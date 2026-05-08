import os
import django

# Setup Django environment
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra.models import CustomerUser
from florra_admin.models import AdminUser

# 1. Create a dummy customer user
customer_email = "test@test.com"
password = "password123"

if not CustomerUser.objects.filter(email=customer_email).exists():
    user = CustomerUser.objects.create_user(
        email=customer_email,
        password=password,
        full_name="Test User",
        mobile="1234567890"
    )
    print(f"✅ Customer created: {customer_email} | Password: {password}")
else:
    print(f"Customer {customer_email} already exists!")

# 2. Create a dummy admin user
admin_email = "admin@test.com"

if not AdminUser.objects.filter(email=admin_email).exists():
    admin = AdminUser(
        email=admin_email,
        full_name="Admin User",
    )
    admin.set_password(password)
    admin.save()
    print(f"✅ Admin created: {admin_email} | Password: {password}")
else:
    print(f"Admin {admin_email} already exists!")
