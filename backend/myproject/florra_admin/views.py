from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from .models import AdminUser, AdminToken
from .serializers import AdminLoginSerializer


class AdminLoginView(APIView):
    def post(self, request):
        print(f"DEBUG ADMIN LOGIN: Data received: {request.data}")
        serializer = AdminLoginSerializer(data=request.data)
        if not serializer.is_valid():
            print(f"DEBUG ADMIN LOGIN: Serializer errors: {serializer.errors}")
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
            
        serializer.is_valid(raise_exception=True)

        email = serializer.validated_data['email']
        password = serializer.validated_data['password']

        try:
            admin = AdminUser.objects.get(email=email, is_active=True)
        except AdminUser.DoesNotExist:
            return Response(
                {"message": "Invalid credentials"},
                status=status.HTTP_400_BAD_REQUEST
            )

        if not admin.check_password(password):
            return Response(
                {"message": "Invalid credentials"},
                status=status.HTTP_400_BAD_REQUEST
            )

        # ✅ USE ONLY AdminToken (NOT Django Token)
        token, _ = AdminToken.objects.get_or_create(admin=admin)

        return Response({
            "token": token.key,
            "email": admin.email,
            "full_name": admin.full_name,
            "user_type": "admin",
            "role": admin.role,
            "can_access_billing": admin.can_access_billing,
            "can_access_reports": admin.can_access_reports,
            "can_access_predictions": admin.can_access_predictions,
            "profile_image": admin.profile_image.url if admin.profile_image else None
        }, status=status.HTTP_200_OK)


from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.parsers import MultiPartParser, FormParser

from .models import Product
from .serializers import ProductSerializer


# 🔹 PRODUCT LIST + SEARCH + FILTER
class ProductListView(APIView):
    def get(self, request):
        queryset = Product.objects.all().order_by('-created_at')

        search = request.GET.get('search')
        category = request.GET.get('category')
        size = request.GET.get('size')
        stock = request.GET.get('stock')

        if search:
            queryset = queryset.filter(tile_name__icontains=search)

        if category:
            queryset = queryset.filter(category=category)

        if size:
            queryset = queryset.filter(size=size)

        if stock == 'low':
            queryset = queryset.filter(stock__lt=20)

        serializer = ProductSerializer(queryset, many=True)
        return Response(serializer.data)


# 🔹 ADD PRODUCT (IMAGE SUPPORT)
class ProductCreateView(APIView):
    parser_classes = (MultiPartParser, FormParser)

    def post(self, request):
        send_notification = request.data.get('send_notification') == 'true'
        serializer = ProductSerializer(data=request.data)
        if serializer.is_valid():
            product = serializer.save()

            if send_notification:
                try:
                    from florra.models import CustomerUser, Notification
                    customers = CustomerUser.objects.all()
                    for customer in customers:
                        Notification.objects.create(
                            user=customer,
                            product=product,
                            title="New Arrival! 🚀",
                            message=f"Exciting news! We just added {product.tile_name} to our catalog. Check it out now!",
                            notification_type="system"
                        )
                except Exception as e:
                    print(f"ERROR: Failed to send broadcast notifications: {e}")

            return Response(
                {"message": "Product added successfully"},
                status=status.HTTP_201_CREATED
            )
        return Response(serializer.errors, status=400)


# 🔹 PRODUCT DETAIL / UPDATE / DELETE
class ProductDetailView(APIView):
    parser_classes = (MultiPartParser, FormParser)

    def get_object(self, pk):
        try:
            return Product.objects.get(pk=pk)
        except Product.DoesNotExist:
            return None

    def get(self, request, pk):
        product = self.get_object(pk)
        if not product:
            return Response({"message": "Not found"}, status=404)

        serializer = ProductSerializer(product)
        return Response(serializer.data)

    # 🔹 FULL UPDATE
    def put(self, request, pk):
        product = self.get_object(pk)
        if not product:
            return Response({"message": "Not found"}, status=404)

        serializer = ProductSerializer(product, data=request.data)
        if serializer.is_valid():
            product = serializer.save()
            
            # Broadcast notification if requested
            send_notification = request.data.get('send_notification')
            if send_notification == 'true':
                try:
                    from florra.models import CustomerUser, Notification
                    customers = CustomerUser.objects.all()
                    for customer in customers:
                        Notification.objects.create(
                            user=customer,
                            title="Product Update!",
                            message=f"Check out the latest updates for {product.tile_name}. Available now!",
                            notification_type="system"
                        )
                except Exception as e:
                    print(f"ERROR: Failed to send broadcast notifications: {e}")

            return Response({"message": "Product updated"})
        return Response(serializer.errors, status=400)

    # 🔹 PARTIAL UPDATE (IMAGE / PRICE / STOCK etc)
    def patch(self, request, pk):
        product = self.get_object(pk)
        if not product:
            return Response({"message": "Not found"}, status=404)

        serializer = ProductSerializer(product, data=request.data, partial=True)
        if serializer.is_valid():
            product = serializer.save()

            # Broadcast notification if requested
            send_notification = request.data.get('send_notification')
            if send_notification == 'true':
                try:
                    from florra.models import CustomerUser, Notification
                    customers = CustomerUser.objects.all()
                    for customer in customers:
                        Notification.objects.create(
                            user=customer,
                            title="Product Restock!",
                            message=f"{product.tile_name} has been updated. Don't miss out!",
                            notification_type="system"
                        )
                except Exception as e:
                    print(f"ERROR: Failed to send broadcast notifications: {e}")

            return Response({"message": "Product partially updated"})
        return Response(serializer.errors, status=400)

    def delete(self, request, pk):
        product = self.get_object(pk)
        if not product:
            return Response({"message": "Not found"}, status=404)

        product.delete()
        return Response({"message": "Product deleted"})


from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.utils.timezone import now
from datetime import timedelta

from .models import Bill
from .serializers import BillSerializer


# 🔹 CREATE BILL (GenerateBillActivity)
class BillCreateView(APIView):
    def post(self, request):
        serializer = BillSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(
                {"message": "Bill saved successfully"},
                status=status.HTTP_201_CREATED
            )
        return Response(serializer.errors, status=400)


# 🔹 LIST BILLS (SavedBillsActivity)
class BillListView(APIView):
    def get(self, request):
        queryset = Bill.objects.all().order_by('-created_at')

        status_filter = request.GET.get('status')
        search = request.GET.get('search')
        this_month = request.GET.get('this_month')

        if status_filter:
            queryset = queryset.filter(status=status_filter)

        if search:
            queryset = queryset.filter(
                bill_no__icontains=search
            )

        if this_month == 'true':
            start_date = now().replace(day=1)
            queryset = queryset.filter(created_at__gte=start_date)

        serializer = BillSerializer(queryset, many=True)
        return Response(serializer.data)


# 🔹 VIEW SINGLE BILL (Preview / View Bill)
class BillDetailView(APIView):
    def get(self, request, pk):
        try:
            bill = Bill.objects.get(pk=pk)
        except Bill.DoesNotExist:
            return Response({"message": "Not found"}, status=404)

        serializer = BillSerializer(bill)
        return Response(serializer.data)


# 🔹 UPDATE STATUS (Paid / Cancelled)
class BillStatusUpdateView(APIView):
    def patch(self, request, pk):
        try:
            bill = Bill.objects.get(pk=pk)
        except Bill.DoesNotExist:
            return Response({"message": "Not found"}, status=404)

        bill.status = request.data.get("status", bill.status)
        bill.save()

        return Response({"message": "Status updated"})


from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

from .models import Enquiry
from .serializers import EnquirySerializer


# 🔹 LIST + FILTER + SEARCH
class EnquiryListView(APIView):
    def get(self, request):
        queryset = Enquiry.objects.all().order_by('-created_at')

        status_param = request.GET.get('status')
        search = request.GET.get('search')

        if status_param:
            queryset = queryset.filter(status=status_param)

        if search:
            queryset = queryset.filter(customer_name__icontains=search)
            
        # 🔒 Customer Privacy: Filter by email if user is logged in
        if request.user.is_authenticated:
            # Only filter if it is NOT an admin
            # (Assuming AdminUser is the model for admins)
            is_admin = isinstance(request.user, AdminUser)
            print(f"DEBUG ENQUIRY LIST: User={request.user}, IsAdmin={is_admin}")
            
            if not is_admin:
                 # Assume customer uses email as identifier
                 queryset = queryset.filter(customer_email=request.user.email)

        serializer = EnquirySerializer(queryset, many=True)
        return Response(serializer.data)


# 🔹 CREATE ENQUIRY (from customer app / website)
class EnquiryCreateView(APIView):
    def post(self, request):
        serializer = EnquirySerializer(data=request.data)
        if serializer.is_valid():
            if request.user.is_authenticated:
                enquiry = serializer.save(customer_email=request.user.email)
            else:
                enquiry = serializer.save()

            # 🔔 Auto-notify admin about new quotation request
            try:
                from .models import AdminNotification
                AdminNotification.objects.create(
                    title=f"New Quotation Request from {enquiry.customer_name}",
                    message=enquiry.message[:200] if enquiry.message else "Customer sent a quotation request.",
                    notification_type="alert",
                    sent_by="System",
                )
            except Exception:
                pass  # Don't fail if notification creation fails

            return Response(
                {"message": "Enquiry submitted"},
                status=status.HTTP_201_CREATED
            )
        return Response(serializer.errors, status=400)


# 🔹 UPDATE STATUS (Admin action)
class EnquiryStatusUpdateView(APIView):
    def patch(self, request, pk):
        try:
            enquiry = Enquiry.objects.get(pk=pk)
        except Enquiry.DoesNotExist:
            return Response({"message": "Not found"}, status=404)

        status_value = request.data.get('status')
        reference = request.data.get('reference')

        if status_value:
            enquiry.status = status_value

        if reference:
            enquiry.reference = reference

        enquiry.save()
        enquiry.save()
        return Response({"message": "Enquiry updated"})


class EnquiryRespondView(APIView):
    def post(self, request):
        enquiry_id = request.data.get('id')
        if not enquiry_id:
            return Response({"error": "Enquiry ID required"}, status=400)

        try:
            enquiry = Enquiry.objects.get(id=enquiry_id)
        except Enquiry.DoesNotExist:
            return Response({"error": "Enquiry not found"}, status=404)

        # Update Quotation Data
        enquiry.quotation_price = request.data.get('quotation_price')
        enquiry.quotation_boxes = request.data.get('quotation_boxes')
        enquiry.quotation_delivery_time = request.data.get('quotation_delivery_time')
        enquiry.quotation_notes = request.data.get('quotation_notes')
        
        enquiry.status = "quoted" # Force status update
        enquiry.save()

        # Return updated object
        return Response(EnquirySerializer(enquiry).data)

from datetime import timedelta
from django.utils import timezone
from django.db.models import Sum, Count

class SalesPredictionView(APIView):
    def get(self, request):
        today = timezone.now().date()
        last_30_days = today - timedelta(days=30)

        # 1. Fetch Bills
        bills = Bill.objects.filter(created_at__date__gte=last_30_days)
        
        # 2. Aggregate Daily Sales
        daily_sales = {}
        for i in range(30):
            day = last_30_days + timedelta(days=i)
            daily_sales[day] = 0.0

        for bill in bills:
            # Aggregate based on date
            b_date = bill.created_at.date()
            if b_date in daily_sales:
                daily_sales[b_date] += float(bill.grand_total)

        # Prepare X (days index) and Y (sales amount) for regression
        x_values = list(range(30))
        y_values = [daily_sales[last_30_days + timedelta(days=i)] for i in x_values]

        # 3. Prediction Logic (Weighted Exponential Forecast)
        n = len(x_values)
        if n > 0:
            # Calculate weights (more recent days have higher weight)
            weights = [1.0 + (i / 30.0) for i in range(30)]
            sum_w = sum(weights)
            sum_wx = sum(w * x for w, x in zip(weights, x_values))
            sum_wy = sum(w * y for w, y in zip(weights, y_values))
            sum_wxy = sum(w * x * y for w, x, y in zip(weights, x_values, y_values))
            sum_wxx = sum(w * x * x for w, x in zip(weights, x_values))

            denominator = sum_w * sum_wxx - sum_wx * sum_wx
            if denominator != 0:
                m = (sum_w * sum_wxy - sum_wx * sum_wy) / denominator
                c = (sum_wy - m * sum_wx) / sum_w
            else:
                m = 0
                c = sum_wy / sum_w
            
            # Clamp m (slope) to avoid extreme predictions
            max_slope = (sum_wy / sum_w) * 0.25 / 7 if n > 0 and sum_wy > 0 else 1.0
            if m > max_slope: m = max_slope
            if m < -max_slope: m = -max_slope
        else:
            m, c = 0, 0

        # 3.5 Day-Of-Week Seasonality Logic
        dow_sales = {i: 0.0 for i in range(7)}
        dow_counts = {i: 0 for i in range(7)}
        for i in range(30):
            day = last_30_days + timedelta(days=i)
            dow = day.weekday()
            dow_sales[dow] += y_values[i]
            dow_counts[dow] += 1
            
        dow_avg = {i: (dow_sales[i] / dow_counts[i] if dow_counts[i] > 0 else 0) for i in range(7)}
        overall_avg = sum(y_values) / len(y_values) if len(y_values) > 0 else 1
        if overall_avg == 0: overall_avg = 1
        
        # Multipliers based on weekday vs weekend behavior
        dow_multiplier = {i: (dow_avg[i] / overall_avg if dow_avg[i] > 0 else 1.0) for i in range(7)}
        for k in dow_multiplier:
            dow_multiplier[k] = max(0.6, min(1.8, dow_multiplier[k])) # Clamp seasonality

        # 4. Leading Indicators (Enquiry Correlation)
        enquiry_count = Enquiry.objects.filter(created_at__date__gte=last_30_days).count()
        conversion_boost = 1.0
        if enquiry_count > 0:
            # If enquiries are 2x higher than usual (baseline 10), boost prediction
            conversion_boost = 1.0 + min(0.20, (enquiry_count / 40.0))

        # Predict Next 30 Days with Boost and Seasonality
        predicted_revenue = 0
        future_y_values = []
        for i in range(30, 60): 
            future_day = last_30_days + timedelta(days=i)
            dow = future_day.weekday()
            
            base_pred_daily = (m * i + c)
            if base_pred_daily < 0: base_pred_daily = 0
            
            # Apply seasonality and boost
            pred_daily = base_pred_daily * dow_multiplier[dow] * conversion_boost
            predicted_revenue += pred_daily
            future_y_values.append(pred_daily)

        # Fallback: Minimum baseline prediction
        current_avg_daily = sum(y_values) / 30 if y_values else 0
        if predicted_revenue < (current_avg_daily * 20): 
            predicted_revenue = current_avg_daily * 30 * 1.10 # Assume 10% growth if math fails
            # Reset future values for charts
            future_y_values = [(predicted_revenue / 30) for _ in range(30)]

        # Growth Percentage
        current_30d_revenue = sum(y_values)
        growth_percentage = 0
        if current_30d_revenue > 0:
            growth_percentage = ((predicted_revenue - current_30d_revenue) / current_30d_revenue) * 100

        # 5. Chart Data (Dynamic Week Aggregates)
        past_points = [
            round(sum(y_values[0:10]) / 10),
            round(sum(y_values[10:20]) / 10),
            round(sum(y_values[20:30]) / 10)
        ]
        
        # Smooth future points using real seasonality-adjusted future values
        future_points = [
            round(sum(future_y_values[0:15]) / 15),
            round(sum(future_y_values[15:30]) / 15)
        ]
        
        chart_data = {
            "week1": past_points[0],
            "week2": past_points[1],
            "week3": past_points[2],
            "week4": future_points[0],
            "week5": future_points[1]
        }

        # 6. Recommendation logic
        enquiry_count = Enquiry.objects.filter(created_at__gte=last_30_days).count()
        total_orders = bills.count()
        
        if growth_percentage > 15 or enquiry_count > (total_orders * 1.5):
            recommendation = f"High demand detected (+{round(growth_percentage)}%). Restock top sellers by 25%."
        elif growth_percentage > 5:
            recommendation = "Steady growth expected. Maintain 10% safety buffer in stock."
        elif growth_percentage < -10:
            recommendation = "Demand slowing down. Clear old stock with targeted discounts."
        else:
            recommendation = "Stable demand. Follow standard replenishment cycles."

        # 7. Market Trends (Top Category & Finish)
        # We need to look at BillItems for bills in the last 30 days
        from .models import BillItem
        
        # Get all bill items for the filtered bills
        recent_items = BillItem.objects.filter(bill__in=bills)
        
        # We need to manually aggregate because BillItem doesn't have direct Category/Finish fields (they are on Product)
        # So we iterate and count. For performance on large data, annotation is better, but this is fine for now.
        category_counts = {}
        finish_counts = {}
        
        for item in recent_items:
            # Find the product specifically by name (since we don't have FK to Product in BillItem model shown in snippets, usually it's there but let's be safe)
            # Actually, BillItem usually has a product FK. The model shown earlier: 
            # item_name = models.CharField... size = ... 
            # It DOES NOT seem to have a Product FK in the snippet provided (lines 119-130 of models.py).
            # It just stores item_name. This is a design flaw but we work with what we have.
            # We will try to match item_name to Product.tile_name
            try:
                # specific product might be deleted, so we use filter().first()
                product = Product.objects.filter(tile_name=item.item_name).first()
                if product:
                    cat = product.category
                    fin = product.finish
                    
                    qty = item.quantity
                    
                    category_counts[cat] = category_counts.get(cat, 0) + qty
                    finish_counts[fin] = finish_counts.get(fin, 0) + qty
            except:
                continue

        # Find top Category
        top_category = "Ceramic" # Default
        top_cat_count = 0
        if category_counts:
            top_category = max(category_counts, key=category_counts.get)
            top_cat_count = category_counts[top_category]

        # Find top Finish
        top_finish = "Glossy" # Default
        top_fin_count = 0
        if finish_counts:
            top_finish = max(finish_counts, key=finish_counts.get)
            top_fin_count = finish_counts[top_finish]
            
        market_trends = [
            {
                "name": top_category,
                "value": f"+{top_cat_count} orders", # Simple representation
                "trend": "up" # Mock trend direction
            },
            {
                "name": f"{top_finish} Finish",
                "value": "High Demand",
                "trend": "up"
            }
        ]

        # 8. Calculate "Actual" Data for Toggle
        from django.db.models.functions import TruncMonth
        
        # Ranges
        start_of_month = today.replace(day=1)
        
        # Last Month
        first_day_last_month = (start_of_month - timedelta(days=1)).replace(day=1)
        end_day_last_month = start_of_month - timedelta(days=1)
        
        # Last 3 Months
        start_3_months = today - timedelta(days=90)
        
        # This Year
        start_of_year = today.replace(month=1, day=1)
        
        # Helpers
        def get_totals(queryset):
            total = 0.0
            for b in queryset:
                total += float(b.grand_total)
            return total

        def get_demand(queryset):
            # Aggregate quantity by item_name
            item_counts = {}
            for bill in queryset:
                bill_items = BillItem.objects.filter(bill=bill)
                for item in bill_items:
                    item_counts[item.item_name] = item_counts.get(item.item_name, 0) + item.quantity
            
            if not item_counts:
                return "N/A", "N/A", "N/A", "N/A"
            
            # Sort by quantity
            sorted_items = sorted(item_counts.items(), key=lambda x: x[1], reverse=True)
            high_name = sorted_items[0][0]
            low_name = sorted_items[-1][0]
            
            # Get Tile Nos
            high_prod = Product.objects.filter(tile_name=high_name).first()
            low_prod = Product.objects.filter(tile_name=low_name).first()
            
            high_no = high_prod.tile_no if high_prod and high_prod.tile_no else ""
            low_no = low_prod.tile_no if low_prod and low_prod.tile_no else ""
            
            return high_name, low_name, high_no, low_no

        def get_chart_data(start_date, end_date):
            # Divide period into 5 buckets
            delta = (end_date - start_date).days + 1
            bucket_size = max(1, delta // 5) 
            
            buckets = [0.0] * 5
            
            # Re-fetch bills for this range efficiently or pass queryset
            # Passing queryset is tricky if we need granular dates, let's filter inside loop or iterate 
            # Better: Iterate all bills in range and assign to bucket
            # But we already have querysets for ranges. Let's start simple: re-query or use existing if small.
            # Given we have the queryset in the main block, let's just use it? No, passed querysets are filtered by date already.
            
            # Let's iterate the specific queryset for that range
            # We need to accept queryset as arg
            pass 

        def get_chart_values(queryset, start, end):
            # 5 buckets
            total_days = (end - start).days + 1
            step = total_days / 5.0
            
            values = [0.0] * 5
            
            for bill in queryset:
                bill_date = bill.created_at.date()
                if bill_date < start or bill_date > end: continue
                
                day_offset = (bill_date - start).days
                bucket_index = int(day_offset / step)
                if bucket_index >= 5: bucket_index = 4 # Handle end edge case
                
                values[bucket_index] += float(bill.grand_total)
            
            return [round(v) for v in values]

        # 1. This Month
        bills_this_month = Bill.objects.filter(created_at__date__gte=start_of_month)
        # 1. This Month
        bills_this_month = Bill.objects.filter(created_at__date__gte=start_of_month)
        val_tm = get_totals(bills_this_month)
        high_tm, low_tm, high_tm_no, low_tm_no = get_demand(bills_this_month)
        chart_tm = get_chart_values(bills_this_month, start_of_month, today)

        # 2. Last Month
        bills_last_month = Bill.objects.filter(
            created_at__date__gte=first_day_last_month,
            created_at__date__lte=end_day_last_month
        )
        val_lm = get_totals(bills_last_month)
        high_lm, low_lm, high_lm_no, low_lm_no = get_demand(bills_last_month)
        chart_lm = get_chart_values(bills_last_month, first_day_last_month, end_day_last_month)

        # 3. Last 3 Months
        bills_3_months = Bill.objects.filter(created_at__date__gte=start_3_months)
        val_3m = get_totals(bills_3_months)
        high_3m, low_3m, high_3m_no, low_3m_no = get_demand(bills_3_months)
        chart_3m = get_chart_values(bills_3_months, start_3_months, today)

        # 4. Yearly
        bills_yearly = Bill.objects.filter(created_at__date__gte=start_of_year)
        val_yr = get_totals(bills_yearly)
        high_yr, low_yr, high_yr_no, low_yr_no = get_demand(bills_yearly)
        chart_yr = get_chart_values(bills_yearly, start_of_year, today)

        actual_data = {
            "this_month": {
                "sales": val_tm,
                "revenue": val_tm,
                "high_demand_product": high_tm,
                "low_demand_product": low_tm,
                "high_demand_tile_no": high_tm_no,
                "low_demand_tile_no": low_tm_no,
                "graph_data": chart_tm
            },
            "last_month": {
                "sales": val_lm,
                "revenue": val_lm,
                "high_demand_product": high_lm,
                "low_demand_product": low_lm,
                "high_demand_tile_no": high_lm_no,
                "low_demand_tile_no": low_lm_no,
                "graph_data": chart_lm
            },
            "last_3_months": {
                "sales": val_3m,
                "revenue": val_3m,
                "high_demand_product": high_3m,
                "low_demand_product": low_3m,
                "high_demand_tile_no": high_3m_no,
                "low_demand_tile_no": low_3m_no,
                "graph_data": chart_3m
            },
            "yearly": {
                "sales": val_yr,
                "revenue": val_yr,
                "high_demand_product": high_yr,
                "low_demand_product": low_yr,
                "high_demand_tile_no": high_yr_no,
                "low_demand_tile_no": low_yr_no,
                "graph_data": chart_yr
            }
        }

        # Global/Predicted Demand (Top 2 across all recent bills)
        high_demand = []
        low_demand = []
        
        # Aggregate all items from bills in last 30 days
        all_item_counts = {}
        for bill in bills:
            for item in BillItem.objects.filter(bill=bill):
                all_item_counts[item.item_name] = all_item_counts.get(item.item_name, 0) + item.quantity
        
        if all_item_counts:
            sorted_all = sorted(all_item_counts.items(), key=lambda x: x[1], reverse=True)
            high_name = sorted_all[0][0]
            low_name = sorted_all[-1][0]
            
            high_demand = Product.objects.filter(tile_name=high_name)
            low_demand = Product.objects.filter(tile_name=low_name)

        # Global/Predicted Tile Nos
        high_pred_no = high_demand[0].tile_no if high_demand and high_demand[0].tile_no else ""
        low_pred_no = low_demand[0].tile_no if low_demand and low_demand[0].tile_no else ""

        return Response({
            "predicted_sales": round(predicted_revenue / 100),
            "estimated_revenue": round(predicted_revenue, 2),
            "growth_percentage": round(growth_percentage, 1),
            "high_demand_product": high_demand[0].tile_name if high_demand else "N/A",
            "low_demand_product": low_demand[0].tile_name if low_demand else "N/A",
            "high_demand_tile_no": high_pred_no,
            "low_demand_tile_no": low_pred_no,
            "stock_suggestion": recommendation,
            "chart": chart_data,
            "market_trends": market_trends,
            "actual_data": actual_data
        })


from rest_framework.views import APIView
from rest_framework.response import Response

from .models import Product
from .serializers import InventoryProductSerializer


class InventoryView(APIView):
    def get(self, request):

        products = Product.objects.filter(is_active=True)

        # 🔍 Search
        search = request.GET.get("search")
        if search:
            products = products.filter(tile_name__icontains=search)

        # 🏷 Category filter (Ceramic / Porcelain / etc.)
        category = request.GET.get("category")
        if category:
            products = products.filter(category=category)

        # 🎨 Finish filter
        finish = request.GET.get("finish")
        if finish:
            products = products.filter(finish=finish)

        # 📊 Inventory stats (SAFE – no 500)
        total = products.count()
        in_stock = products.filter(stock__gte=20).count()
        low_stock = products.filter(stock__gt=0, stock__lt=20).count()
        empty = products.filter(stock=0).count()

        serializer = InventoryProductSerializer(products, many=True)

        return Response({
            "stats": {
                "total": total,
                "in_stock": in_stock,
                "low_stock": low_stock,
                "empty": empty
            },
            "products": serializer.data
        })


# 🔹 CUSTOMER FEATURES (Restored)
from rest_framework.parsers import MultiPartParser, FormParser
# from .serializers import CustomerProfileSerializer # Removed
# from .models import Customer # Removed if not used elsewhere

class ImageSearchProductView(APIView):
    parser_classes = (MultiPartParser, FormParser)
    
    def post(self, request, *args, **kwargs):
        # Mock logic: Return random products
        products = Product.objects.all()[:5]
        serializer = ProductSerializer(products, many=True)
        return Response(serializer.data)

class CustomerNotificationView(APIView):
    def get(self, request):
        # Mock notifications
        data = [
            {
                "id": 1,
                "title": "Welcome to Florra",
                "message": "Explore our wide range of premium tiles.",
                "date": "Now",
                "isRead": False
            },
            {
                "id": 2, 
                "title": "New Arrivals", 
                "message": "Check out our latest ceramic collection.", 
                "date": "Yesterday", 
                "isRead": True
            }
        ]
        return Response(data)


# ─────────────────────────────────────────
# 🔔 ADMIN NOTIFICATION MANAGEMENT
# ─────────────────────────────────────────
from .models import AdminNotification

class AdminNotificationListView(APIView):
    """GET all notifications sent by admin (system log)"""
    def get(self, request):
        notifications = AdminNotification.objects.all().order_by('-created_at')
        data = []
        for n in notifications:
            data.append({
                "id": n.id,
                "title": n.title,
                "message": n.message,
                "type": n.notification_type,
                "sent_by": n.sent_by,
                "timestamp": n.created_at.strftime("%Y-%m-%d %H:%M"),
            })
        return Response(data)


class AdminNotificationCreateView(APIView):
    """POST: Admin creates a new notification broadcast to all customers"""
    def post(self, request):
        title = request.data.get('title', '').strip()
        message = request.data.get('message', '').strip()
        notif_type = request.data.get('type', 'system').strip()
        product_id = request.data.get('product_id')

        if not title or not message:
            return Response(
                {"error": "Title and message are required"},
                status=status.HTTP_400_BAD_REQUEST
            )

        try:
            # Fetch product if provided
            from florra.models import Product
            product_instance = None
            if product_id:
                try:
                    product_instance = Product.objects.get(id=product_id)
                except Product.DoesNotExist:
                    pass

            # Save to AdminNotification log
            notification = AdminNotification.objects.create(
                title=title,
                message=message,
                notification_type=notif_type,
            )
            
            # Also push to ALL registered customers via the florra Notification model
            from florra.models import CustomerUser, Notification as CustomerNotification
            customers = CustomerUser.objects.filter(is_active=True)
            created_count = 0
            for customer in customers:
                CustomerNotification.objects.create(
                    user=customer,
                    title=title,
                    message=message,
                    notification_type=notif_type,
                    is_read=False,
                    product=product_instance
                )
                created_count += 1

            return Response({
                "message": f"Notification sent to {created_count} customer(s).",
                "notification_id": notification.id
            }, status=status.HTTP_201_CREATED)

        except Exception as e:
            import traceback
            return Response(
                {"error": str(e), "traceback": traceback.format_exc()},
            )


from .serializers import AdminUserSerializer

class StaffListView(APIView):
    def get(self, request):
        staff = AdminUser.objects.all().order_by('-created_at')
        serializer = AdminUserSerializer(staff, many=True)
        return Response(serializer.data)

    def post(self, request):
        email = request.data.get('email')
        password = request.data.get('password')
        full_name = request.data.get('full_name')
        role = request.data.get('role', 'staff')

        if not email or not password or not full_name:
            return Response({"error": "All fields are required"}, status=status.HTTP_400_BAD_REQUEST)

        if AdminUser.objects.filter(email=email).exists():
            return Response({"error": "Email already exists"}, status=status.HTTP_400_BAD_REQUEST)

        user = AdminUser(
            email=email,
            full_name=full_name,
            role=role,
            can_access_billing=request.data.get('can_access_billing', False),
            can_access_reports=request.data.get('can_access_reports', False),
            can_access_predictions=request.data.get('can_access_predictions', False)
        )
        user.set_password(password)
        user.save()

        serializer = AdminUserSerializer(user)
        return Response(serializer.data, status=status.HTTP_201_CREATED)


class StaffDetailView(APIView):
    def put(self, request, pk):
        try:
            user = AdminUser.objects.get(pk=pk)
        except AdminUser.DoesNotExist:
            return Response({"error": "Not found"}, status=status.HTTP_404_NOT_FOUND)

        email = request.data.get('email')
        full_name = request.data.get('full_name')
        role = request.data.get('role')
        password = request.data.get('password')

        if email:
            if AdminUser.objects.filter(email=email).exclude(pk=pk).exists():
                return Response({"error": "Email already exists"}, status=status.HTTP_400_BAD_REQUEST)
            user.email = email

        if full_name:
            user.full_name = full_name

        if role:
            user.role = role

        if password:
            user.set_password(password)

        can_access_billing = request.data.get('can_access_billing')
        if can_access_billing is not None:
            user.can_access_billing = can_access_billing

        can_access_reports = request.data.get('can_access_reports')
        if can_access_reports is not None:
            user.can_access_reports = can_access_reports

        can_access_predictions = request.data.get('can_access_predictions')
        if can_access_predictions is not None:
            user.can_access_predictions = can_access_predictions

        user.save()
        serializer = AdminUserSerializer(user)
        return Response(serializer.data)

    def delete(self, request, pk):
        try:
            user = AdminUser.objects.get(pk=pk)
        except AdminUser.DoesNotExist:
            return Response({"error": "Not found"}, status=status.HTTP_404_NOT_FOUND)

        user.delete()
        return Response({"message": "Staff member deleted successfully"})
