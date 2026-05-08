import os
import sys
import pickle
import numpy as np
import django
from PIL import Image

# Setup Django
sys.path.append(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))) # Add project root
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product
from florra.ai.feature_extractor import extract_features
from django.conf import settings

# Paths
# MEDIA_ROOT is available in settings
MEDIA_ROOT = settings.MEDIA_ROOT
VECTOR_FILE = os.path.join(settings.BASE_DIR, "florra/ai/tile_vectors.pkl")

import logging

# Setup Logging
logging.basicConfig(
    filename='build_vectors.log',
    filemode='w',
    format='%(message)s',
    level=logging.INFO,
    encoding='utf-8' # Python 3.9+
)

tile_vectors = {}

print(f"🔍 Scanning Products from Database... (Logging to build_vectors.log)")
logging.info(f"🔍 Scanning Products from Database...")

count = 0
missing_count = 0

products = Product.objects.filter(is_active=True)
logging.info(f"   Found {products.count()} active products in DB.")

for product in products:
    if not product.image:
        logging.warning(f"   ⚠️ Product '{product.tile_name}' (ID: {product.id}) has no image. Skipping.")
        continue

    # product.image.path gives absolute filesystem path
    try:
        img_path = product.image.path
        if not os.path.exists(img_path):
            logging.error(f"   ❌ Image file missing for '{product.tile_name}' (ID: {product.id})")
            logging.error(f"      Expected path: {img_path}")
            # Try to fix path if it's using double project structure
            # e.g. "my app/myproject/myproject/media" instead of "my app/myproject/media"
            
            # Debug: print cwd and media root
            logging.info(f"      CWD: {os.getcwd()}")
            logging.info(f"      MEDIA_ROOT: {MEDIA_ROOT}")
            
            missing_count += 1
            continue
            
        logging.info(f"   Processing: {product.tile_name} ({os.path.basename(img_path)})...")
        
        # Open original image once
        from PIL import Image
        try:
            original_img = Image.open(img_path).convert("RGB")
        except Exception as e:
            logging.error(f"   ❌ Error opening image for ID {product.id}: {e}")
            continue

        # Generate variations
        # Variation: Center Crop (80%)
        width, height = original_img.size
        left = width * 0.1
        top = height * 0.1
        right = width * 0.9
        bottom = height * 0.9
        center_crop = original_img.crop((left, top, right, bottom))
        
        variations = [
            original_img,
            center_crop,  # Added Crop
            original_img.rotate(90, expand=True),
            original_img.rotate(180, expand=True),
            original_img.rotate(270, expand=True),
            original_img.transpose(Image.FLIP_LEFT_RIGHT)
        ]
        
        product_vectors = []
        for i, img_var in enumerate(variations):
            vec = extract_features(image_path=img_var) # extract_features now accepts PIL Image in image_path param via my hack
            if vec is not None:
                product_vectors.append(vec)
        
        if product_vectors:
             # Use the relative filename (basename) as the key
            img_name = os.path.basename(img_path)
            tile_vectors[img_name] = product_vectors
            count += 1
            logging.info(f"      Mapped {len(product_vectors)} variations.")
            
    except Exception as e:
        logging.error(f"   ❌ Error processing ID {product.id}: {e}")

if count > 0:
    with open(VECTOR_FILE, "wb") as f:
        pickle.dump(tile_vectors, f)
    logging.info(f"✅ Indexed {count} tiles (with augmentations). Saved to {VECTOR_FILE}")
else:
    logging.warning("⚠️ No images successfully processed.")

if missing_count > 0:
    logging.warning(f"⚠️ {missing_count} product images were missing from disk.")
