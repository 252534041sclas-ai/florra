import os
import django
import json

# Setup Django environment
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra.models import Product

def import_data():
    with open('inventory_dump.json', 'r') as f:
        data = json.load(f)
    
    products_added = 0
    for item in data.get('products', []):
        try:
            # Handle empty strings and default to None for integer fields if necessary
            stock = int(item.get('stock', 0)) if item.get('stock') else 0
            price = float(item.get('price', 0)) if item.get('price') else 0.0

            # Create or update the product
            Product.objects.update_or_create(
                tile_no=str(item.get('id', '')),  # using id as tile_no just in case
                defaults={
                    'tile_name': item.get('tile_name', ''),
                    'brand_name': item.get('brand_name', ''),
                    'category': item.get('category', ''),
                    'size': item.get('size', ''),
                    'finish': item.get('finish', ''),
                    'color': item.get('color', ''),
                    'price': price,
                    'stock': stock,
                    'stock_status': item.get('stock_status', ''),
                    # Skip image for now since they are missing from local disk anyway
                }
            )
            products_added += 1
        except Exception as e:
            print(f"Error adding {item.get('tile_name')}: {e}")
            
    print(f"Successfully imported {products_added} products!")

if __name__ == '__main__':
    print("Starting import...")
    import_data()
