import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Enquiry

old_email = "test@test.com"
new_email = "asskrvsdhana1215@gmail.com"

# Update Enquiries (which seem to be used as Quotations)
enquiries = Enquiry.objects.filter(customer_email=old_email)
count = enquiries.count()
enquiries.update(customer_email=new_email)
print(f"Updated {count} Enquiry/Quotation records from {old_email} to {new_email}.")

# Let's also check if there is a Quotation model just in case
try:
    from florra.models import Quotation
    quotations = Quotation.objects.filter(customer_email=old_email)
    q_count = quotations.count()
    quotations.update(customer_email=new_email)
    print(f"Updated {q_count} Quotation records from {old_email} to {new_email}.")
except ImportError:
    pass

# Also check for Bill model just in case it's linked by string
from florra_admin.models import Bill
bills = Bill.objects.filter(customer_name=old_email) # unlikely, bills use customer_name / phone
b_count = bills.count()
if b_count > 0:
    bills.update(customer_name=new_email)
    print(f"Updated {b_count} Bill records.")

