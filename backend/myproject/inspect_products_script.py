from florra_admin.models import Product
import os

print(f"Total Products: {Product.objects.count()}")

last_product = Product.objects.last()
if last_product:
    print(f"Last Product ID: {last_product.id}")
    print(f"Name: {last_product.tile_name}")
    print(f"Image Field: {last_product.image}")
    try:
        path = last_product.image.path
        print(f"Absolute Path: {path}")
        print(f"Exists: {os.path.exists(path)}")
    except Exception as e:
        print(f"Error getting path: {e}")
else:
    print("No products in DB")
