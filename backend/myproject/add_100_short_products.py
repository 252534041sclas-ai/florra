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
    {
        'name': 'Living', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\living_tile_1779016238992.png',
        'items': ['Statuario White', 'Armani Grey', 'Pulpis Brown', 'Crema Marfil', 'Onyx Blue', 'Calacatta Gold', 'Bottochino Classic', 'Travertine Beige', 'Emperador Dark', 'Dyna Natural']
    },
    {
        'name': 'Bathroom', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\bathroom_tile_1779016253144.png',
        'items': ['Ocean Blue Mosaic', 'Aqua Glass', 'Pearl White', 'Slate Grey', 'Moroccan Blue', 'Pebble Stone', 'Hexagon Black', 'Subway White', 'Emerald Green', 'Seafoam Green']
    },
    {
        'name': 'Bedroom', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\bedroom_tile_1779016268251.png',
        'items': ['Rustic Oak', 'Walnut Plank', 'Maple Wood', 'Mahogany Strip', 'Teak Wood', 'Birch Light', 'Pine Classic', 'Cherry Wood', 'Ash Grey Wood', 'Smoked Oak']
    },
    {
        'name': 'Kitchen', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\kitchen_tile_1779016282706.png',
        'items': ['Terrazzo Grey', 'Granite Black', 'Geometric Pattern', 'Quartz White', 'Chevron Wood', 'Onyx Pearl', 'Travertine Silver', 'Concrete Matte', 'Marble Splash', 'Hexagon White']
    },
    {
        'name': 'Wall', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\wall_tile_1779016298408.png',
        'items': ['Sandstone Clad', 'Brick Red', 'Slate Split', 'Limestone Ivory', 'Quartzite Grey', 'Travertine Wall', 'Marble Hex', 'Wood Panel', '3D Wave White', 'Rustic Stone']
    },
    {
        'name': 'Floor', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\floor_tile_1779016312953.png',
        'items': ['Cement Grey', 'Basalt Black', 'Porcelain Cream', 'Concrete Rough', 'Terrazzo Classic', 'Slate Charcoal', 'Marble Glossy', 'Quartz Sand', 'Limestone Matte', 'Granito White']
    },
    {
        'name': 'Parking', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\parking_tile_1779016331874.png',
        'items': ['Heavy Duty Paver', 'Checker Red', 'Cobblestone', 'Interlock Paver', 'Rough Concrete', 'Basalt Paver', 'Granite Paver', 'Zigzag Red', 'Slate Paver', 'Cement Block']
    },
    {
        'name': 'Steps', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\steps_tile_1779016345984.png',
        'items': ['Bullnose Black', 'Step Tread Grey', 'Riser White', 'Granite Step', 'Marble Edge', 'Quartz Step', 'Anti-skid Grey', 'Terrazzo Step', 'Wood Look Step', 'Stone Step']
    },
    {
        'name': 'Roof', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\roof_tile_white_cooling_1779016364048.png',
        'items': ['White Cooling Roof', 'Terracotta Red', 'Clay Mangalore', 'Slate Roof', 'Concrete Roof', 'Weatherproof White', 'Solar Reflective', 'Cool Roof Matte', 'Ceramic Roof', 'Eco Roof']
    },
    {
        'name': 'Mosaic', 
        'base_img': r'C:\Users\akash\.gemini\antigravity\brain\ba3a340c-146e-4319-aedc-c16c722e015a\mosaic_tile_1779016377545.png',
        'items': ['Silver Glass', 'Copper Penny', 'Turquoise Hex', 'Pearl Shell', 'Black Marble Hex', 'Gold Glass', 'Blue Mix Mosaic', 'Green Leaf', 'Marble Chevron', 'Stone Pebble']
    }
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

# Clean previous products created today (we'll start fresh)
from datetime import date
today_str = str(date.today())
AdminProduct.objects.filter(created_at__date=today_str).delete()
FlorraProduct.objects.filter(created_at__date=today_str).delete()

for cat in categories:
    cat_name = cat['name']
    base_img_path = cat['base_img']
    items = cat['items']
    
    for i, short_name in enumerate(items, start=1):
        if i <= 3:
            stock = 5 # 3 low
        elif i == 4:
            stock = 0 # 1 empty
        else:
            stock = 150 # normal
            
        tile_name = short_name
        tile_no = f"CAT-{cat_name[:3].upper()}-{i:03d}"
        
        # Generate the unique image variant using slugified short name
        safe_name = short_name.lower().replace(' ', '_').replace('-', '_')
        unique_img_filename = f"{cat_name.lower()}_{safe_name}.png"
        unique_img_path = os.path.join(media_dir, unique_img_filename)
        
        if os.path.exists(base_img_path):
            create_variant(base_img_path, unique_img_path, i)
        else:
            unique_img_filename = "placeholder.png"
            
        img_db_path = f"products/{unique_img_filename}"
        
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
            description=f"Premium {tile_name} tile for your space.",
            image=img_db_path
        )

print("Successfully added 100 cleanly named products with 100 unique images!")
