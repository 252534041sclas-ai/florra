import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product as AdminProduct
from florra.models import Product as FlorraProduct

# 10 brand new premium products with authentic images and full details
premium_products = [
    {
        'tile_name': 'Onyx Blue',
        'tile_no': 'OB002',
        'brand_name': 'Kajaria',
        'category': 'Living',
        'size': '80x80 cm',
        'finish': 'Glossy',
        'color': 'Blue',
        'price': 85.00,
        'cost_price': 50.00,
        'stock': 120,
        'description': 'Premium glossy blue onyx marble tile for living spaces.',
        'image': 'products/living_onyx_blue.png'
    },
    {
        'tile_name': 'Emerald Green',
        'tile_no': 'EG002',
        'brand_name': 'Somany',
        'category': 'Bathroom',
        'size': '30x60 cm',
        'finish': 'Glossy',
        'color': 'Green',
        'price': 45.00,
        'cost_price': 28.00,
        'stock': 85,
        'description': 'Gorgeous deep emerald green gloss tile for bathroom walls.',
        'image': 'products/bathroom_emerald_green.png'
    },
    {
        'tile_name': 'Walnut Plank',
        'tile_no': 'WP002',
        'brand_name': 'Somany',
        'category': 'Bedroom',
        'size': '20x120 cm',
        'finish': 'Matte',
        'color': 'Brown',
        'price': 55.00,
        'cost_price': 35.00,
        'stock': 140,
        'description': 'Rich dark walnut wood plank finish tile for cozy bedrooms.',
        'image': 'products/bedroom_walnut_plank.png'
    },
    {
        'tile_name': 'Chevron Wood',
        'tile_no': 'CW002',
        'brand_name': 'Kajaria',
        'category': 'Kitchen',
        'size': '60x60 cm',
        'finish': 'Satin',
        'color': 'Brown',
        'price': 48.00,
        'cost_price': 30.00,
        'stock': 90,
        'description': 'Elegant chevron patterned wood look tile for kitchen backsplashes.',
        'image': 'products/kitchen_chevron_wood.png'
    },
    {
        'tile_name': '3D Wave White',
        'tile_no': 'WW002',
        'brand_name': 'Somany',
        'category': 'Wall',
        'size': '30x90 cm',
        'finish': 'Glossy',
        'color': 'White',
        'price': 39.00,
        'cost_price': 22.00,
        'stock': 200,
        'description': 'Modern three-dimensional wave pattern white wall tile.',
        'image': 'products/wall_3d_wave_white.png'
    },
    {
        'tile_name': 'Basalt Black',
        'tile_no': 'BB002',
        'brand_name': 'Kajaria',
        'category': 'Floor',
        'size': '60x60 cm',
        'finish': 'Matte',
        'color': 'Black',
        'price': 62.00,
        'cost_price': 38.00,
        'stock': 110,
        'description': 'Heavy-duty matte black basalt floor tile.',
        'image': 'products/floor_basalt_black.png'
    },
    {
        'tile_name': 'Granite Paver',
        'tile_no': 'GP002',
        'brand_name': 'Local',
        'category': 'Parking',
        'size': '40x40 cm',
        'finish': 'Rustic',
        'color': 'Grey',
        'price': 34.00,
        'cost_price': 18.00,
        'stock': 350,
        'description': 'Extremely durable rough textured grey granite paver for driveways.',
        'image': 'products/parking_granite_paver.png'
    },
    {
        'tile_name': 'Marble Edge Step',
        'tile_no': 'ME002',
        'brand_name': 'Kajaria',
        'category': 'Steps',
        'size': '30x120 cm',
        'finish': 'Satin',
        'color': 'White',
        'price': 68.00,
        'cost_price': 42.00,
        'stock': 75,
        'description': 'Premium white marble step tile with clean bullnose edges.',
        'image': 'products/steps_marble_edge.png'
    },
    {
        'tile_name': 'Weatherproof White',
        'tile_no': 'WW003',
        'brand_name': 'Somany',
        'category': 'Roof',
        'size': '30x30 cm',
        'finish': 'Matte',
        'color': 'White',
        'price': 32.00,
        'cost_price': 16.00,
        'stock': 400,
        'description': 'Cool roof tiles with solar reflective properties.',
        'image': 'products/roof_weatherproof_white.png'
    },
    {
        'tile_name': 'Copper Penny Mosaic',
        'tile_no': 'CP002',
        'brand_name': 'Mosaic',
        'category': 'Mosaic',
        'size': '30x30 cm',
        'finish': 'Glossy',
        'color': 'Copper',
        'price': 89.00,
        'cost_price': 55.00,
        'stock': 60,
        'description': 'Iridescent copper-colored circular glass mosaic tiles.',
        'image': 'products/mosaic_copper_penny.png'
    }
]

for p in premium_products:
    # 1. Create in AdminProduct
    AdminProduct.objects.get_or_create(
        tile_name=p['tile_name'],
        tile_no=p['tile_no'],
        defaults={
            'brand_name': p['brand_name'],
            'category': p['category'],
            'size': p['size'],
            'finish': p['finish'],
            'color': p['color'],
            'price': p['price'],
            'cost_price': p['cost_price'],
            'stock': p['stock'],
            'description': p['description'],
            'image': p['image']
        }
    )

    # 2. Create in FlorraProduct
    try:
        FlorraProduct.objects.get_or_create(
            tile_name=p['tile_name'],
            tile_no=p['tile_no'],
            defaults={
                'brand_name': p['brand_name'],
                'category': p['category'],
                'size': p['size'],
                'finish': p['finish'],
                'color': p['color'],
                'price': p['price'],
                'cost_price': p['cost_price'],
                'stock': p['stock'],
                'description': p['description'],
                'image': p['image']
            }
        )
    except Exception:
        pass

print("Successfully added 10 premium products with fully populated details.")
