import os
import django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

with open("db_images_output.txt", "w", encoding="utf-8") as f:
    f.write(f"{'ID':<5} | {'Name':<20} | {'Image Path'}\n")
    f.write("-" * 60 + "\n")

    for p in Product.objects.all():
        img_str = str(p.image) if p.image else "NONE"
        f.write(f"{p.id:<5} | {p.tile_name[:20]:<20} | {img_str}\n")
