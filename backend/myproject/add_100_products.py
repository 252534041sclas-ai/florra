import os
import django
import shutil

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product as AdminProduct
from florra.models import Product as FlorraProduct

categories = [
    {
        'name': 'Living',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\living_tile_1779016238992.png',
        'desc': 'Premium living room floor tile'
    },
    {
        'name': 'Bathroom',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\bathroom_tile_1779016253144.png',
        'desc': 'Anti-slip bathroom tile'
    },
    {
        'name': 'Bedroom',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\bedroom_tile_1779016268251.png',
        'desc': 'Warm wood finish bedroom tile'
    },
    {
        'name': 'Kitchen',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\kitchen_tile_1779016282706.png',
        'desc': 'Geometric kitchen backsplash'
    },
    {
        'name': 'Wall',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\wall_tile_1779016298408.png',
        'desc': 'Natural stone wall cladding'
    },
    {
        'name': 'Floor',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\floor_tile_1779016312953.png',
        'desc': 'Heavy duty porcelain floor tile'
    },
    {
        'name': 'Parking',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\parking_tile_1779016331874.png',
        'desc': 'Thick parking paver'
    },
    {
        'name': 'Steps',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\steps_tile_1779016345984.png',
        'desc': 'Bullnose step tile'
    },
    {
        'name': 'Roof',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\roof_tile_white_cooling_1779016364048.png',
        'desc': 'White cooling roof tile'
    },
    {
        'name': 'Mosaic',
        'image': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\mosaic_tile_1779016377545.png',
        'desc': 'Iridescent glass mosaic'
    }
]

media_dir = os.path.join('media', 'products')
os.makedirs(media_dir, exist_ok=True)

for cat in categories:
    cat_name = cat['name']
    image_path = cat['image']
    desc = cat['desc']
    
    dest_img = os.path.join(media_dir, f"{cat_name.lower()}_base.png")
    if os.path.exists(image_path):
        shutil.copy(image_path, dest_img)
    else:
        print(f"Warning: Image {image_path} not found.")
        
    for i in range(1, 11):
        if i <= 3:
            stock = 5 # 3 low stock
        elif i == 4:
            stock = 0 # 1 empty stock
        else:
            stock = 150 # 6 normal stock
            
        if cat_name == 'Roof' and i == 1:
            tile_name = "Premium White Cooling Roof Tile"
        else:
            tile_name = f"{cat_name} {desc} Variant {i}"
            
        tile_no = f"CAT-{cat_name[:3].upper()}-{i:03d}"
        
        AdminProduct.objects.create(
            tile_name=tile_name,
            tile_no=tile_no,
            brand_name="Florra Premium",
            category=cat_name,
            size="60x60 cm",
            finish="Glossy" if cat_name != 'Roof' else "Matte",
            color="Mixed",
            price=50.00 + i,
            stock=stock,
            description=f"Beautiful {cat_name.lower()} tile. {desc}.",
            image=f"products/{cat_name.lower()}_base.png"
        )
        
        try:
            FlorraProduct.objects.create(
                tile_name=tile_name,
                tile_no=tile_no,
                brand_name="Florra Premium",
                category=cat_name,
                size="60x60 cm",
                finish="Glossy" if cat_name != 'Roof' else "Matte",
                color="Mixed",
                price=50.00 + i,
                stock=stock,
                description=f"Beautiful {cat_name.lower()} tile. {desc}.",
                image=f"products/{cat_name.lower()}_base.png"
            )
        except Exception as e:
            pass

print("Successfully added 100 products.")
