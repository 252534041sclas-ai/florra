import os
import sys
import django

sys.path.append(r'c:\Users\suriy\OneDrive\Desktop\my app\myproject')
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

print(f"{'ID':<5} {'Tile Name':<30} {'Image Path':<50}")
print("-" * 85)
for p in Product.objects.all():
    print(f"{p.id:<5} {p.tile_name[:30]:<30} {str(p.image):<50}")
