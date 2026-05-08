import os
import django
import sys

# Setup Django
sys.path.append(r"c:\Users\suriy\OneDrive\Desktop\my app\myproject")
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

print("Checking Product images...")
products = Product.objects.all()[:10]
for p in products:
    print(f"ID: {p.id}, Name: {p.tile_name}, Image: {p.image}")
