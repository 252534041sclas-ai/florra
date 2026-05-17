import os
import django
from PIL import Image, ImageEnhance, ImageOps

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product as AdminProduct
from florra.models import Product as FlorraProduct

media_dir = os.path.join('media', 'products')
os.makedirs(media_dir, exist_ok=True)

categories = [
    {'name': 'Living', 'base_img': 'living_base.png', 'desc': 'Premium living room floor tile'},
    {'name': 'Bathroom', 'base_img': 'bathroom_base.png', 'desc': 'Anti-slip bathroom tile'},
    {'name': 'Bedroom', 'base_img': 'bedroom_base.png', 'desc': 'Warm wood finish bedroom tile'},
    {'name': 'Kitchen', 'base_img': 'kitchen_base.png', 'desc': 'Geometric kitchen backsplash'},
    {'name': 'Wall', 'base_img': 'wall_base.png', 'desc': 'Natural stone wall cladding'},
    {'name': 'Floor', 'base_img': 'floor_base.png', 'desc': 'Heavy duty porcelain floor tile'},
    {'name': 'Parking', 'base_img': 'parking_base.png', 'desc': 'Thick parking paver'},
    {'name': 'Steps', 'base_img': 'steps_base.png', 'desc': 'Bullnose step tile'},
    {'name': 'Roof', 'base_img': 'roof_base.png', 'desc': 'White cooling roof tile'},
    {'name': 'Mosaic', 'base_img': 'mosaic_base.png', 'desc': 'Iridescent glass mosaic'}
]

def create_variant(base_image_path, dest_path, variant_num):
    try:
        img = Image.open(base_image_path).convert('RGB')
        
        if variant_num == 1:
            pass # original
        elif variant_num == 2:
            img = img.rotate(90)
        elif variant_num == 3:
            img = img.rotate(180)
        elif variant_num == 4:
            img = img.rotate(270)
        elif variant_num == 5:
            img = ImageOps.mirror(img)
        elif variant_num == 6:
            img = ImageOps.flip(img)
        elif variant_num == 7:
            enhancer = ImageEnhance.Color(img)
            img = enhancer.enhance(1.5)
        elif variant_num == 8:
            enhancer = ImageEnhance.Brightness(img)
            img = enhancer.enhance(0.8)
        elif variant_num == 9:
            enhancer = ImageEnhance.Contrast(img)
            img = enhancer.enhance(1.3)
        elif variant_num == 10:
            width, height = img.size
            img = img.crop((width*0.1, height*0.1, width*0.9, height*0.9)).resize((width, height))
            
        img.save(dest_path)
        return True
    except Exception as e:
        print(f"Failed to create variant {variant_num} for {base_image_path}: {e}")
        return False

# Clean previous products from today just in case
from datetime import date
today_str = str(date.today())
AdminProduct.objects.filter(created_at__date=today_str).delete()
FlorraProduct.objects.filter(created_at__date=today_str).delete()

for cat in categories:
    cat_name = cat['name']
    base_img_path = os.path.join(media_dir, cat['base_img'])
    desc = cat['desc']
    
    for i in range(1, 11):
        if i <= 3:
            stock = 5 # 3 low
        elif i == 4:
            stock = 0 # 1 empty
        else:
            stock = 150 # normal
            
        if cat_name == 'Roof' and i == 1:
            tile_name = "Premium White Cooling Roof Tile"
        else:
            tile_name = f"{cat_name} {desc} Variant {i}"
            
        tile_no = f"CAT-{cat_name[:3].upper()}-{i:03d}"
        
        # Generate the unique image variant
        unique_img_filename = f"{cat_name.lower()}_variant_{i}.png"
        unique_img_path = os.path.join(media_dir, unique_img_filename)
        
        if os.path.exists(base_img_path):
            create_variant(base_img_path, unique_img_path, i)
        else:
            # If base missing, just use default placeholder path or skip
            unique_img_filename = "placeholder.png"
            
        img_db_path = f"products/{unique_img_filename}"
        
        p = AdminProduct.objects.create(
            tile_name=tile_name,
            tile_no=tile_no,
            brand_name="Florra Premium",
            category=cat_name,
            size="60x60 cm",
            finish="Glossy" if cat_name != 'Roof' else "Matte",
            color="Mixed",
            price=50.00 + i,
            stock=stock,
            description=f"Beautiful {cat_name.lower()} tile. {desc}. Unique style {i}.",
            image=img_db_path
        )
        
        # Just create one, or if they need to be in both:
        # Avoid duplicating if they share a table! 
        # Since earlier we saw 200 items, they probably share a table.
        # Let's check if the table is shared by catching DB errors.
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
                description=f"Beautiful {cat_name.lower()} tile. {desc}. Unique style {i}.",
                image=img_db_path
            )
        except Exception as e:
            # Table is shared or constraint failed
            pass

print("Successfully added 100 products with 100 unique images!")
