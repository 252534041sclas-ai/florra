import os
import django

# Set up Django environment
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

tile_nos = ["CW001", "BM002", "RO003", "GC004", "BM005", "BT006", "CG007", "CS008", "CM009", "RT010"]

# Try to delete from florra_admin
try:
    from florra_admin.models import Product as AdminProduct
    deleted_count, _ = AdminProduct.objects.filter(tile_no__in=tile_nos).delete()
    print(f"Deleted {deleted_count} products from florra_admin.Product.")
except Exception as e:
    print(f"Error deleting from florra_admin: {e}")

# Try to delete from florra
try:
    from florra.models import Product as FlorraProduct
    deleted_count, _ = FlorraProduct.objects.filter(tile_no__in=tile_nos).delete()
    print(f"Deleted {deleted_count} products from florra.Product.")
except Exception as e:
    print(f"Error deleting from florra: {e}")

print("Deletion script execution complete.")
