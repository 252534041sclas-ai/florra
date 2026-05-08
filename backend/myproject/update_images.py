import os
import django

# Set up Django environment
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

image_mapping = {
    "CW001": "products/carrara_white.png",
    "BM002": "products/black_marquina.png",
    "RO003": "products/rustic_oak.png",
    "GC004": "products/grey_concrete.png",
    "BM005": "products/blue_mosaic.png",
    "BT006": "products/beige_travertine.png",
    "CG007": "products/calacatta_gold.png",
    "CS008": "products/charcoal_slate.png",
    "CM009": "products/cream_marble.png",
    "RT010": "products/red_terracotta.png",
}

for tile_no, image_path in image_mapping.items():
    product = Product.objects.filter(tile_no=tile_no).first()
    if product:
        product.image = image_path
        product.save()
        print(f"Updated image for {product.tile_name}")
    else:
        print(f"Product with tile_no {tile_no} not found")

print("Finished updating product images.")
