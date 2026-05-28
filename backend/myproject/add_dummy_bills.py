import os
import django
import random
from decimal import Decimal
from datetime import timedelta
from django.utils import timezone

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Bill, BillItem, Product

def generate_bills():
    products = list(Product.objects.filter(is_active=True)[:20])
    if not products:
        print("No products available to create bills.")
        return

    customers = [
        {"name": "Muthu", "phone": "9998887776", "address": "10, South St, Salem"},
        {"name": "Dinesh", "phone": "8887776665", "address": "22, West Mada St, Chennai"},
        {"name": "Balaji", "phone": "7776665554", "address": "5, Gandhi Rd, Madurai"},
        {"name": "Vijay", "phone": "6665554443", "address": "33, Main Rd, Erode"},
        {"name": "Prakash", "phone": "5554443332", "address": "12, Cross St, Trichy"}
    ]

    # Generate 15 bills across the last 30 days
    for i in range(15):
        customer = random.choice(customers)
        
        last_bill = Bill.objects.order_by('id').last()
        if last_bill and last_bill.bill_no.startswith('B-'):
            try:
                next_no = int(last_bill.bill_no.split('-')[1]) + 1
            except ValueError:
                next_no = 1005 + i
        else:
            next_no = 1005 + i
        bill_no = f"B-{next_no}"

        bill = Bill.objects.create(
            bill_no=bill_no,
            customer_name=customer["name"],
            customer_phone=customer["phone"],
            customer_address=customer["address"],
            subtotal=0,
            gst_percentage=18,
            gst_amount=0,
            discount=0,
            loading=0,
            grand_total=0,
            status='Paid'
        )
        
        # Simulate a date between 1 and 30 days ago
        random_days_ago = random.randint(1, 30)
        bill.created_at = timezone.now() - timedelta(days=random_days_ago)
        bill.bill_date = bill.created_at
        bill.save()

        # Update the created_at field properly since auto_now_add can override it on first save
        Bill.objects.filter(id=bill.id).update(created_at=bill.created_at, bill_date=bill.bill_date)

        subtotal = Decimal('0.00')
        
        num_items = random.randint(1, 4)
        bill_products = random.sample(products, min(num_items, len(products)))
        
        for prod in bill_products:
            qty = random.randint(5, 40)
            rate = prod.price
            amount = rate * qty
            
            BillItem.objects.create(
                bill=bill,
                item_name=prod.tile_name,
                size=prod.size,
                quantity=qty,
                rate=rate,
                amount=amount
            )
            subtotal += amount
            
            # Reduce stock
            prod.stock -= qty
            prod.save()
            
        gst_amount = subtotal * Decimal('0.18')
        grand_total = subtotal + gst_amount
        
        bill.subtotal = subtotal
        bill.gst_amount = gst_amount
        bill.grand_total = grand_total
        bill.save()
        
        print(f"Created bill {bill_no} on {bill.created_at.strftime('%Y-%m-%d')} for {customer['name']} with total {grand_total}")

if __name__ == '__main__':
    generate_bills()
