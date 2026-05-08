import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

print("Searching for 'Img:' in all product fields...")
p = Product.objects.all()
for item in p:
    for field in item._meta.fields:
        val = getattr(item, field.name)
        if val and 'Img:' in str(val):
            print(f"Product ID: {item.id}, Field: {field.name}, Value: {val}")
