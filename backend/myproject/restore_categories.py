import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

updates = {
    'CW001': 'Floor',
    'BM002': 'Floor',
    'RO003': 'Living',
    'GC004': 'Floor',
    'BM005': 'Bathroom',
    'BT006': 'Wall',
    'CG007': 'Floor',
    'CS008': 'Floor',
    'CM009': 'Bedroom',
    'RT010': 'Floor'
}

for t_no, cat in updates.items():
    Product.objects.filter(tile_no=t_no).update(category=cat)

Product.objects.filter(tile_name='ush').update(category='Living')

print("Successfully restored original categories.")
