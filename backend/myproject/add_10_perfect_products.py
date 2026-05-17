import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product as AdminProduct
from florra.models import Product as FlorraProduct
from datetime import date

# Clean up all products created today to remove duplicates/reused image products
today_str = str(date.today())
AdminProduct.objects.filter(created_at__date=today_str).delete()
try:
    FlorraProduct.objects.filter(created_at__date=today_str).delete()
except Exception:
    pass

# The 10 perfect, highly authentic images
perfect_products = [
    {
        'tile_name': 'Carrara White',
        'tile_no': 'CW001',
        'category': 'Living',
        'size': '60x60 cm',
        'finish': 'Glossy',
        'price': 45.50,
        'stock': 150,
        'image': 'products/carrara_white.png'
    },
    {
        'tile_name': 'Blue Mosaic',
        'tile_no': 'BM001',
        'category': 'Bathroom',
        'size': '30x30 cm',
        'finish': 'Glossy',
        'price': 25.00,
        'stock': 5, # Low stock
        'image': 'products/blue_mosaic.png'
    },
    {
        'tile_name': 'Rustic Oak',
        'tile_no': 'RO001',
        'category': 'Bedroom',
        'size': '20x100 cm',
        'finish': 'Matte',
        'price': 38.00,
        'stock': 200,
        'image': 'products/rustic_oak.png'
    },
    {
        'tile_name': 'Black Marquina',
        'tile_no': 'BM002',
        'category': 'Kitchen',
        'size': '60x120 cm',
        'finish': 'Glossy',
        'price': 65.00,
        'stock': 0, # Empty stock
        'image': 'products/black_marquina.png'
    },
    {
        'tile_name': 'Charcoal Slate',
        'tile_no': 'CS001',
        'category': 'Wall',
        'size': '60x60 cm',
        'finish': 'Rustic',
        'price': 48.00,
        'stock': 120,
        'image': 'products/charcoal_slate.png'
    },
    {
        'tile_name': 'Grey Concrete',
        'tile_no': 'GC001',
        'category': 'Floor',
        'size': '60x60 cm',
        'finish': 'Matte',
        'price': 42.00,
        'stock': 150,
        'image': 'products/grey_concrete.png'
    },
    {
        'tile_name': 'Beige Travertine',
        'tile_no': 'BT001',
        'category': 'Parking',
        'size': '30x60 cm',
        'finish': 'Rustic',
        'price': 35.00,
        'stock': 5, # Low stock
        'image': 'products/beige_travertine.png'
    },
    {
        'tile_name': 'Cream Marble',
        'tile_no': 'CM001',
        'category': 'Steps',
        'size': '60x60 cm',
        'finish': 'Satin',
        'price': 50.00,
        'stock': 90,
        'image': 'products/cream_marble.png'
    },
    {
        'tile_name': 'Red Terracotta',
        'tile_no': 'RT001',
        'category': 'Roof',
        'size': '30x30 cm',
        'finish': 'Matte',
        'price': 28.00,
        'stock': 0, # Empty stock
        'image': 'products/red_terracotta.png'
    },
    {
        'tile_name': 'Calacatta Gold',
        'tile_no': 'CG001',
        'category': 'Mosaic',
        'size': '80x80 cm',
        'finish': 'Glossy',
        'price': 75.00,
        'stock': 150,
        'image': 'products/calacatta_gold.png'
    }
]

for p in perfect_products:
    AdminProduct.objects.create(
        tile_name=p['tile_name'],
        tile_no=p['tile_no'],
        brand_name="Florra Premium",
        category=p['category'],
        size=p['size'],
        finish=p['finish'],
        color="Mixed",
        price=p['price'],
        stock=p['stock'],
        description=f"Authentic {p['tile_name']} tile.",
        image=p['image']
    )

print("Successfully added 10 PERFECT products with 1-to-1 image mapping.")
