import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product as AdminProduct

# Mapping the 10 authentic images to 10 categories
authentic_mappings = [
    {
        'category': 'Living',
        'base_name': 'Carrara White',
        'image': 'products/carrara_white.png'
    },
    {
        'category': 'Bathroom',
        'base_name': 'Blue Mosaic',
        'image': 'products/blue_mosaic.png'
    },
    {
        'category': 'Bedroom',
        'base_name': 'Rustic Oak',
        'image': 'products/rustic_oak.png'
    },
    {
        'category': 'Kitchen',
        'base_name': 'Black Marquina',
        'image': 'products/black_marquina.png'
    },
    {
        'category': 'Wall',
        'base_name': 'Charcoal Slate',
        'image': 'products/charcoal_slate.png'
    },
    {
        'category': 'Floor',
        'base_name': 'Grey Concrete',
        'image': 'products/grey_concrete.png'
    },
    {
        'category': 'Parking',
        'base_name': 'Beige Travertine',
        'image': 'products/beige_travertine.png'
    },
    {
        'category': 'Steps',
        'base_name': 'Cream Marble',
        'image': 'products/cream_marble.png'
    },
    {
        'category': 'Roof',
        'base_name': 'Red Terracotta',
        'image': 'products/red_terracotta.png'
    },
    {
        'category': 'Mosaic',
        'base_name': 'Calacatta Gold',
        'image': 'products/calacatta_gold.png'
    }
]

variant_suffixes = [
    "Premium Glossy",
    "Standard Matte",
    "Large Format",
    "Small Format",
    "Textured Finish",
    "Commercial Grade",
    "Anti-Slip Surface",
    "Polished Slab",
    "Rustic Finish",
    "Luxury Edition"
]

# Clean previous products created today (we'll start fresh)
from datetime import date
today_str = str(date.today())
AdminProduct.objects.filter(created_at__date=today_str).delete()

for mapping in authentic_mappings:
    cat_name = mapping['category']
    base_name = mapping['base_name']
    img_db_path = mapping['image']
    
    for i, suffix in enumerate(variant_suffixes, start=1):
        if i <= 3:
            stock = 5 # 3 low
        elif i == 4:
            stock = 0 # 1 empty
        else:
            stock = 150 # normal
            
        tile_name = f"{base_name} {suffix}"
        tile_no = f"CAT-{cat_name[:3].upper()}-{i:03d}"
        
        AdminProduct.objects.create(
            tile_name=tile_name,
            tile_no=tile_no,
            brand_name="Florra Premium",
            category=cat_name,
            size="60x60 cm",
            finish="Glossy" if 'Glossy' in suffix else "Matte",
            color="Mixed",
            price=50.00 + i,
            stock=stock,
            description=f"Authentic {tile_name} tile for your space.",
            image=img_db_path
        )

print("Successfully added 100 products with perfectly matching authentic images and names!")
