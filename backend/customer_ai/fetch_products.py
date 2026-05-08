import pymysql
import json
import os

# Database Credentials (from settings.py)
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'akash123',
    'database': 'florra',
    'port': 3306
}

# Connect to MySQL
try:
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()
    print("✅ Connected to MySQL Database")
except pymysql.MySQLError as e:
    print(f"❌ Connection Failed: {e}")
    exit(1)

# Query Products
# Django table name: florra_product
try:
    cursor.execute("SELECT id, tile_name, category, size, finish, image FROM florra_product")
    products = cursor.fetchall()
except pymysql.MySQLError as e:
    print(f"❌ SQL Error: {e}")
    exit(1)

product_list = []
# Assuming 'media' folder is in ../myproject/media
MEDIA_ROOT = os.path.abspath("../myproject/media")

for p in products:
    image_rel_path = p[5]
    if image_rel_path:
        # Django ImageField stores relative path like 'products/image.jpg'
        full_image_path = os.path.join(MEDIA_ROOT, image_rel_path).replace("\\", "/")
    else:
        full_image_path = None

    product_list.append({
        "id": p[0],
        "name": p[1],
        "category": p[2],
        "size": p[3],
        "finish": p[4],
        "image_path": full_image_path,
        # Rich text representation for CLIP text encoder
        "text_representation": f"A {p[4]} finish {p[2]} tile named {p[1]}. Size: {p[3]}."
    })

conn.close()

# Save to JSON
os.makedirs("data", exist_ok=True)
with open("data/products.json", "w", encoding="utf-8") as f:
    json.dump(product_list, f, indent=4)

print(f"✅ Exported {len(product_list)} products to data/products.json")
