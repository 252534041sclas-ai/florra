import os
import django

# Set up Django environment
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

products_data = [
    {"tile_name": "Carrara White", "tile_no": "CW001", "brand_name": "Somany", "category": "Floor", "size": "60x60 cm", "finish": "Glossy", "color": "White", "price": 45.50, "stock": 100, "description": "High quality white marble finish tile."},
    {"tile_name": "Black Marquina", "tile_no": "BM002", "brand_name": "Kajaria", "category": "Floor", "size": "60x120 cm", "finish": "Glossy", "color": "Black", "price": 65.00, "stock": 50, "description": "Elegant black tile with white veins."},
    {"tile_name": "Rustic Oak", "tile_no": "RO003", "brand_name": "Somany", "category": "Living", "size": "20x100 cm", "finish": "Matte", "color": "Brown", "price": 38.00, "stock": 200, "description": "Wood finish tile for a warm look."},
    {"tile_name": "Grey Concrete", "tile_no": "GC004", "brand_name": "Kajaria", "category": "Floor", "size": "60x60 cm", "finish": "Matte", "color": "Grey", "price": 42.00, "stock": 150, "description": "Modern concrete look for industrial spaces."},
    {"tile_name": "Blue Mosaic", "tile_no": "BM005", "brand_name": "Mosaic", "category": "Bathroom", "size": "30x30 cm", "finish": "Glossy", "color": "Blue", "price": 25.00, "stock": 300, "description": "Small blue mosaic tiles for walls."},
    {"tile_name": "Beige Travertine", "tile_no": "BT006", "brand_name": "Somany", "category": "Wall", "size": "30x60 cm", "finish": "Rustic", "color": "Beige", "price": 35.00, "stock": 80, "description": "Natural stone look beige tile."},
    {"tile_name": "Calacatta Gold", "tile_no": "CG007", "brand_name": "Kajaria", "category": "Floor", "size": "80x80 cm", "finish": "Glossy", "color": "White/Gold", "price": 75.00, "stock": 40, "description": "Premium white tile with gold veins."},
    {"tile_name": "Charcoal Slate", "tile_no": "CS008", "brand_name": "Somany", "category": "Floor", "size": "60x60 cm", "finish": "Rustic", "color": "Charcoal", "price": 48.00, "stock": 120, "description": "Dark grey slate finish for outdoors."},
    {"tile_name": "Cream Marble", "tile_no": "CM009", "brand_name": "Kajaria", "category": "Bedroom", "size": "60x60 cm", "finish": "Satin", "color": "Cream", "price": 50.00, "stock": 90, "description": "Smooth cream marble for bedrooms."},
    {"tile_name": "Red Terracotta", "tile_no": "RT010", "brand_name": "Local", "category": "Floor", "size": "30x30 cm", "finish": "Matte", "color": "Red", "price": 28.00, "stock": 500, "description": "Traditional terracotta tiles."},
]

for data in products_data:
    Product.objects.create(**data)

print("Successfully added 10 products.")
