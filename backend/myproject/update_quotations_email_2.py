import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

old_email = "test@test.com"
new_email = "asskrvsdhana1215@gmail.com"

try:
    from florra.models import Quotation
    quotations = Quotation.objects.filter(email=old_email)
    q_count = quotations.count()
    quotations.update(email=new_email)
    print(f"Updated {q_count} Quotation records from {old_email} to {new_email}.")
except ImportError:
    print("Quotation model not found.")
except Exception as e:
    print(e)
