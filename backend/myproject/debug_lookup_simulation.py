import os
import sys
import django

# Setup Django
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))) # Add project root
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

# Filename from the log
target_filename = "JPEG_20260109_085603_334427194804778618.jpg"

with open("lookup_debug.txt", "w", encoding="utf-8") as f:
    f.write(f"Target Filename: '{target_filename}'\n")
    f.write("-" * 50 + "\n")

    # 1. Try exact filter
    f.write("\n1. Testing filter(image__icontains=target_filename)...\n")
    found = Product.objects.filter(image__icontains=target_filename)
    f.write(f"   Found {found.count()} matches.\n")
    for p in found:
        f.write(f"   - ID: {p.id}, Name: {p.tile_name}\n")

    # 3. Inspect Product ID 6 directly
    try:
        if Product.objects.filter(id=6).exists():
            p6 = Product.objects.get(id=6)
            f.write(f"\n3. Inspecting Product ID 6 directly:\n")
            f.write(f"   ID: 6\n")
            f.write(f"   Image Field: '{p6.image}'\n")
            f.write(f"   Image Name:  '{p6.image.name}'\n")
            
            p6_hex = p6.image.name.encode('utf-8').hex()
            target_hex = target_filename.encode('utf-8').hex()
            
            f.write(f"   Hex dump of DB filename: {p6_hex}\n")
            f.write(f"   Hex dump of Target:      {target_hex}\n")
            
            if target_hex in p6_hex:
                f.write("   [MATCH] Target hex found in DB hex.\n")
            else:
                f.write("   [FAIL] Target hex NOT found in DB hex.\n")
            
    except Exception as e:
         f.write(f"Error: {e}\n")
