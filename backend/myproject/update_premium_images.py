import os
import shutil
import django

# Set up Django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product as AdminProduct
from florra.models import Product as FlorraProduct

# Source directory containing generated images
src_dir = r"C:\Users\akash\.gemini\antigravity\brain\8983feb4-1f2f-4de3-a6a8-4e993eacc773"
dest_dir = r"c:\Users\akash\Desktop\project\florra\backend\myproject\media\products"
os.makedirs(dest_dir, exist_ok=True)

# Map product SKUs to generated filenames
image_mapping = {
    'OB002': {
        'src_pattern': 'onyx_blue_custom',
        'dest_filename': 'onyx_blue_custom.png',
        'db_path': 'products/onyx_blue_custom.png'
    },
    'EG002': {
        'src_pattern': 'emerald_green_custom',
        'dest_filename': 'emerald_green_custom.png',
        'db_path': 'products/emerald_green_custom.png'
    },
    'WP002': {
        'src_pattern': 'walnut_plank_custom',
        'dest_filename': 'walnut_plank_custom.png',
        'db_path': 'products/walnut_plank_custom.png'
    },
    'CW002': {
        'src_pattern': 'chevron_wood_custom',
        'dest_filename': 'chevron_wood_custom.png',
        'db_path': 'products/chevron_wood_custom.png'
    },
    'WW002': {
        'src_pattern': 'wave_white_custom',
        'dest_filename': 'wave_white_custom.png',
        'db_path': 'products/wave_white_custom.png'
    },
    'BB002': {
        'src_pattern': 'basalt_black_custom',
        'dest_filename': 'basalt_black_custom.png',
        'db_path': 'products/basalt_black_custom.png'
    },
    'GP002': {
        'src_pattern': 'granite_paver_custom',
        'dest_filename': 'granite_paver_custom.png',
        'db_path': 'products/granite_paver_custom.png'
    },
    'ME002': {
        'src_pattern': 'marble_edge_custom',
        'dest_filename': 'marble_edge_custom.png',
        'db_path': 'products/marble_edge_custom.png'
    },
    'WW003': {
        'src_pattern': 'weatherproof_white_custom',
        'dest_filename': 'weatherproof_white_custom.png',
        'db_path': 'products/weatherproof_white_custom.png'
    },
    'CP002': {
        'src_pattern': 'copper_penny_custom',
        'dest_filename': 'copper_penny_custom.png',
        'db_path': 'products/copper_penny_custom.png'
    }
}

# Scan source directory to find the actual files
all_files = os.listdir(src_dir)

for sku, info in image_mapping.items():
    pattern = info['src_pattern']
    dest_file = info['dest_filename']
    db_path = info['db_path']
    
    # Find matching source file (which has timestamp in it)
    matching_src = None
    for f in all_files:
        if f.startswith(pattern) and f.endswith('.png'):
            matching_src = f
            break
            
    if matching_src:
        src_path = os.path.join(src_dir, matching_src)
        dest_path = os.path.join(dest_dir, dest_file)
        
        # Copy file to media
        shutil.copy2(src_path, dest_path)
        print(f"Copied {matching_src} to {dest_file}")
        
        # Update AdminProduct DB
        admin_updated = AdminProduct.objects.filter(tile_no=sku).update(image=db_path)
        
        # Update FlorraProduct DB
        try:
            florra_updated = FlorraProduct.objects.filter(tile_no=sku).update(image=db_path)
        except Exception:
            florra_updated = 0
            
        print(f"Updated SKU {sku}: Admin updated={admin_updated}, Florra updated={florra_updated}")
    else:
        print(f"ERROR: Could not find generated image for pattern: {pattern}")

print("Custom high-resolution product image update complete.")
