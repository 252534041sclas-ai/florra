
import os
import django

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "myproject.settings")
django.setup()

from florra_admin.models import AdminUser

email = "admin@florra.com"
password = "admin123"
full_name = "Admin User"

try:
    if AdminUser.objects.filter(email=email).exists():
        print(f"User {email} already exists.")
    else:
        user = AdminUser(email=email, full_name=full_name)
        user.set_password(password)
        user.save()
        print(f"Successfully created admin user: {email}")
except Exception as e:
    print(f"Error creating user: {e}")
