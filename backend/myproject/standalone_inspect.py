import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

print("--- Inspecting florra_admin.Product ---")
try:
    from florra_admin.models import Product as AdminProduct
    all_admin = AdminProduct.objects.all()
    print(f"Total Admin products: {all_admin.count()}")
    for p in all_admin:
        print(f"ID: {p.id} | Tile No: {p.tile_no} | Name: {p.tile_name} | Brand: {p.brand_name}")
except Exception as e:
    print(f"Error inspecting florra_admin: {e}")

print("--- Inspecting florra.Product ---")
try:
    from florra.models import Product as FlorraProduct
    all_florra = FlorraProduct.objects.all()
    print(f"Total Florra products: {all_florra.count()}")
    for p in all_florra:
        print(f"ID: {p.id} | Tile No: {p.tile_no} | Name: {p.tile_name} | Brand: {p.brand_name}")
except Exception as e:
    print(f"Error inspecting florra: {e}")
