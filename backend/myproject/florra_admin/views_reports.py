from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.db.models import Sum, Count, F
from django.utils import timezone
from datetime import datetime, timedelta
from .models import Bill, BillItem, Enquiry, Product
from florra.models import Quotation, CustomerUser
import io
from django.http import FileResponse
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import A4
from reportlab.lib import colors
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

class MonthlySalesSummaryView(APIView):
    def get(self, request):
        month = request.GET.get('month', timezone.now().month)
        year = request.GET.get('year', timezone.now().year)
        
        try:
            month = int(month)
            year = int(year)
        except ValueError:
            return Response({"error": "Invalid month or year"}, status=400)

        # Filters
        bills = Bill.objects.filter(created_at__month=month, created_at__year=year)
        enquiries = Enquiry.objects.filter(created_at__month=month, created_at__year=year)
        quotations = Quotation.objects.filter(created_at__month=month, created_at__year=year)

        # Summary Calculations
        total_enquiries = enquiries.count()
        total_quotations = quotations.count()
        confirmed_orders = bills.filter(status='Paid').count()
        pending_quotations = quotations.filter(status='PENDING').count()
        cancelled_quotations = quotations.filter(status='REJECTED').count()
        
        total_revenue = bills.aggregate(Sum('grand_total'))['grand_total__sum'] or 0
        
        # Profit Calculation
        # We need to iterate over bill items to get cost vs price
        total_cost = 0
        for bill in bills:
            for item in bill.items.all():
                # Try to find product cost
                product = Product.objects.filter(tile_name=item.item_name).first()
                if product:
                    total_cost += (product.cost_price * item.quantity)
                else:
                    # Fallback to 70% of price as cost if product not found
                    total_cost += (item.rate * item.quantity * 0.7)
        
        profit = float(total_revenue) - float(total_cost)
        
        conversion_rate = 0
        if total_enquiries > 0:
            conversion_rate = (confirmed_orders / total_enquiries) * 100

        # AI Insights & Growth
        # Compare with previous month
        prev_month = month - 1 if month > 1 else 12
        prev_year = year if month > 1 else year - 1
        
        prev_bills = Bill.objects.filter(created_at__month=prev_month, created_at__year=prev_year)
        prev_revenue = prev_bills.aggregate(Sum('grand_total'))['grand_total__sum'] or 0
        
        growth_pct = 0
        if prev_revenue > 0:
            growth_pct = ((float(total_revenue) - float(prev_revenue)) / float(prev_revenue)) * 100
        
        insight = f"Sales {'increased' if growth_pct >= 0 else 'decreased'} {abs(round(growth_pct, 1))}% compared to last month."
        if growth_pct > 20:
            insight += " Exceptional growth! Consider scaling your top-performing categories."
        elif growth_pct < -10:
            insight += " Slow month. Try launching a promotion to boost engagement."

        return Response({
            "summary": {
                "total_enquiries": total_enquiries,
                "total_quotations": total_quotations,
                "confirmed_orders": confirmed_orders,
                "pending_quotations": pending_quotations,
                "cancelled_quotations": cancelled_quotations,
                "total_revenue": round(float(total_revenue), 2),
                "profit": round(profit, 2),
                "conversion_percentage": round(conversion_rate, 1)
            },
            "insights": {
                "growth_percentage": round(growth_pct, 1),
                "ai_insight": insight
            }
        })

class SalesAnalyticsView(APIView):
    def get(self, request):
        month = request.GET.get('month', timezone.now().month)
        year = request.GET.get('year', timezone.now().year)
        
        # Revenue by Week (Mock buckets for the selected month)
        # In a real app, we'd group by day/week
        bills = Bill.objects.filter(created_at__month=month, created_at__year=year)
        
        revenue_graph = [0] * 4 # 4 weeks
        for bill in bills:
            day = bill.created_at.day
            week_idx = min((day - 1) // 7, 3)
            revenue_graph[week_idx] += float(bill.grand_total)
            
        # Quotation Status distribution
        quotations = Quotation.objects.filter(created_at__month=month, created_at__year=year)
        q_stats = quotations.values('status').annotate(count=Count('id'))
        
        # Best Selling Products
        best_sellers = []
        bill_items = BillItem.objects.filter(bill__in=bills).values('item_name').annotate(total_qty=Sum('quantity')).order_by('-total_qty')[:5]
        for item in bill_items:
            best_sellers.append({
                "name": item['item_name'],
                "quantity": item['total_qty']
            })

        return Response({
            "revenue_chart": revenue_graph,
            "quotation_distribution": {item['status']: item['count'] for item in q_stats},
            "best_sellers": best_sellers
        })

class CustomerReportView(APIView):
    def get(self, request):
        month = request.GET.get('month', timezone.now().month)
        year = request.GET.get('year', timezone.now().year)
        
        # New vs Repeat
        # A customer is "New" if their first bill is in this month
        customers = CustomerUser.objects.all()
        new_count = 0
        repeat_count = 0
        
        # Top Customers by revenue this month
        top_customers = Bill.objects.filter(created_at__month=month, created_at__year=year) \
            .values('customer_name', 'customer_phone') \
            .annotate(total_orders=Count('id'), total_amount=Sum('grand_total')) \
            .order_by('-total_amount')[:10]

        # Pending Follow-ups (Enquiries not yet quoted or resolved)
        pending_followups = Enquiry.objects.filter(status__in=['new', 'follow_up']).count()

        return Response({
            "stats": {
                "new_customers": new_count, # Mock for now
                "repeat_customers": repeat_count,
                "pending_followups": pending_followups
            },
            "top_customers": top_customers
        })

class ExportReportPDFView(APIView):
    def get(self, request):
        try:
            month = int(request.GET.get('month', timezone.now().month))
            year = int(request.GET.get('year', timezone.now().year))
        except (ValueError, TypeError):
            return Response({"error": "Invalid month or year parameters"}, status=400)
        
        buffer = io.BytesIO()
        doc = SimpleDocTemplate(buffer, pagesize=A4)
        styles = getSampleStyleSheet()
        elements = []

        # Title
        month_name = datetime(year, month, 1).strftime('%B')
        elements.append(Paragraph(f"Florra Sales Report - {month_name} {year}", styles['Title']))
        elements.append(Spacer(1, 12))

        # Summary Table
        bills = Bill.objects.filter(created_at__month=month, created_at__year=year)
        total_rev = bills.aggregate(Sum('grand_total'))['grand_total__sum'] or 0
        
        data = [
            ["Metric", "Value"],
            ["Total Revenue", f"Rs. {total_rev}"],
            ["Total Orders", str(bills.count())],
            ["Total Enquiries", str(Enquiry.objects.filter(created_at__month=month, created_at__year=year).count())]
        ]
        
        t = Table(data, colWidths=[200, 200])
        t.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
            ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('GRID', (0, 0), (-1, -1), 1, colors.black)
        ]))
        elements.append(t)
        elements.append(Spacer(1, 24))

        # Recent Orders
        elements.append(Paragraph("Top Orders", styles['Heading2']))
        order_data = [["Bill No", "Customer", "Amount", "Status"]]
        for bill in bills.order_by('-grand_total')[:10]:
            order_data.append([bill.bill_no, bill.customer_name, str(bill.grand_total), bill.status])
        
        ot = Table(order_data, colWidths=[100, 150, 100, 100])
        ot.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.darkblue),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('GRID', (0, 0), (-1, -1), 0.5, colors.grey)
        ]))
        elements.append(ot)

        doc.build(elements)
        buffer.seek(0)
        return FileResponse(buffer, as_attachment=True, filename=f"Florra_Report_{month_name}_{year}.pdf")
