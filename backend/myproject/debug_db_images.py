import os
import sys
import django
from django.conf import settings

# Setup Django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

print(f"{'ID':<5} | {'Name':<20} | {'Image Path'}")
print("-" * 60)

for p in Product.objects.all():
    img_str = str(p.image) if p.image else "NONE"
    print(f"{p.id:<5} | {p.tile_name[:20]:<20} | {img_str}")
