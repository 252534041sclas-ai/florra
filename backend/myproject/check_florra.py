import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

print("Checking all products for 'florra'...")
p = Product.objects.all()
for item in p:
    for field in item._meta.fields:
        val = getattr(item, field.name)
        if 'florra' in str(val).lower():
            print(f"Product ID: {item.id}, Name: {item.tile_name}, Field: {field.name}, Value: {val}")

print("\nChecking categories of all products...")
for item in p:
    print(f"ID: {item.id}, Name: {item.tile_name}, Category: {item.category}, Brand: {item.brand_name}")
