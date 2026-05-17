import os
import django
from datetime import date

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product as AdminProduct
from florra.models import Product as FlorraProduct

today_str = str(date.today())

admin_deleted = AdminProduct.objects.filter(created_at__date=today_str).delete()
print(f"Admin products deleted: {admin_deleted}")

florra_deleted = FlorraProduct.objects.filter(created_at__date=today_str).delete()
print(f"Florra products deleted: {florra_deleted}")

