import pymysql
import os

# Database Credentials
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': 'akash123',
    'database': 'florra',
    'port': 3306
}

# Connect
conn = pymysql.connect(**DB_CONFIG)
cursor = conn.cursor()

# Sample Data
samples = [
    ("Marmo White", "Italian", "60x60", "Glossy", "White", "Floor", "products/marmo_white.jpg"),
    ("Black Pearl", "Granite", "120x60", "Matte", "Black", "Wall", "products/black_pearl.jpg"),
    ("Wooden Strip", "Ceramic", "10x60", "Wood", "Brown", "Floor", "products/wooden_strip.jpg"),
    ("Blue Lagoon", "Mosaic", "30x30", "Glossy", "Blue", "Bathroom", "products/blue_lagoon.jpg"),
]

print("🌱 Seeding database...")
for s in samples:
    # Check if exists
    cursor.execute("SELECT id FROM florra_product WHERE tile_name = %s", (s[0],))
    if not cursor.fetchone():
        sql = """
            INSERT INTO florra_product 
            (tile_name, brand_name, size, finish, color, category, image) 
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(sql, s)
        print(f"   Added {s[0]}")
    else:
        print(f"   Skipped {s[0]} (Already exists)")

conn.commit()
conn.close()
print("✅ Seeding Complete!")
