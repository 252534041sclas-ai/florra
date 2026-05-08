from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import AllowAny, IsAuthenticated
from django.core.mail import send_mail
import random

from .models import CustomerUser, VerificationOTP
from rest_framework.authtoken.models import Token
from .serializers import CustomerRegisterSerializer, CustomerLoginSerializer

# Create your views here.
class SendOTPView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get("email")
        purpose = request.data.get("purpose")  # 'register' or 'reset'

        if not email or not purpose:
            return Response({"message": "Email and purpose required"}, status=status.HTTP_400_BAD_REQUEST)

        if purpose == 'register':
            if CustomerUser.objects.filter(email=email).exists():
                return Response({"message": "Email already registered"}, status=status.HTTP_400_BAD_REQUEST)
        elif purpose == 'reset':
            if not CustomerUser.objects.filter(email=email).exists():
                return Response({"message": "Email not found"}, status=status.HTTP_404_NOT_FOUND)
        else:
            return Response({"message": "Invalid purpose"}, status=status.HTTP_400_BAD_REQUEST)

        # Generate 6-digit OTP
        otp = str(random.randint(100000, 999999))
        
        # Save or update OTP in DB
        VerificationOTP.objects.update_or_create(
            email=email,
            purpose=purpose,
            defaults={"otp": otp, "is_verified": False}
        )

        # Send Email
        subject = f"Your OTP for {purpose.capitalize()}"
        message = f"Your OTP is: {otp}"
        try:
            send_mail(subject, message, 'admin@florra.com', [email], fail_silently=False)
            print(f"DEBUG OTP for {email} ({purpose}): {otp}") # Still print for console backend
            return Response({"message": "OTP sent successfully. Please check your terminal/email."})
        except Exception as e:
            return Response({"message": f"Error sending email: {str(e)}"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class CustomerRegisterView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get("email")
        otp = request.data.get("otp")
        
        if not email or not otp:
            return Response({"message": "Email and OTP are required"}, status=status.HTTP_400_BAD_REQUEST)

        # Verify OTP
        otp_record = VerificationOTP.objects.filter(email=email, otp=otp, purpose='register').first()
        if not otp_record:
            return Response({"message": "Invalid or expired OTP"}, status=status.HTTP_400_BAD_REQUEST)

        serializer = CustomerRegisterSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            otp_record.delete() # Consume OTP
            return Response({"message": "Account created successfully"}, status=status.HTTP_201_CREATED)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class CustomerLoginView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = CustomerLoginSerializer(data=request.data)
        if not serializer.is_valid():
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

        email = serializer.validated_data["email"]
        password = serializer.validated_data["password"]

        try:
            user = CustomerUser.objects.get(email=email)
            if not user.check_password(password):
                return Response({"message": "Invalid credentials"}, status=status.HTTP_400_BAD_REQUEST)
            
            if not user.is_active:
                return Response({"message": "Account is disabled"}, status=status.HTTP_400_BAD_REQUEST)

            token, _ = Token.objects.get_or_create(user=user)
            return Response({
                "token": token.key,
                "email": user.email,
                "full_name": user.full_name,
                "user_type": "customer"
            })
        except CustomerUser.DoesNotExist:
            return Response({"message": "Invalid credentials"}, status=status.HTTP_400_BAD_REQUEST)


class ResetPasswordOTPView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get("email")
        otp = request.data.get("otp")
        new_password = request.data.get("new_password")

        if not all([email, otp, new_password]):
            return Response({"message": "Email, OTP and new password are required"}, status=status.HTTP_400_BAD_REQUEST)

        otp_record = VerificationOTP.objects.filter(email=email, otp=otp, purpose='reset').first()
        if not otp_record:
            return Response({"message": "Invalid or expired OTP"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            user = CustomerUser.objects.get(email=email)
            user.set_password(new_password)
            user.save()
            otp_record.delete()
            return Response({"message": "Password reset successfully"})
        except CustomerUser.DoesNotExist:
            return Response({"message": "User not found"}, status=status.HTTP_404_NOT_FOUND)


class CustomerChangePasswordView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        old_password = request.data.get("old_password")
        new_password = request.data.get("new_password")

        if not old_password or not new_password:
            return Response({"message": "Old and new password required"}, status=status.HTTP_400_BAD_REQUEST)

        user = request.user
        if not user.check_password(old_password):
            return Response({"message": "Incorrect old password"}, status=status.HTTP_400_BAD_REQUEST)

        user.set_password(new_password)
        user.save()
        return Response({"message": "Password changed successfully"})


from rest_framework.parsers import MultiPartParser, FormParser

class CustomerUpdateProfileView(APIView):
    permission_classes = [IsAuthenticated]
    parser_classes = [MultiPartParser, FormParser]

    def post(self, request):
        user = request.user
        data = request.data
        
        # Update text fields
        if "full_name" in data:
            user.full_name = data["full_name"]
        if "mobile" in data:
            user.mobile = data["mobile"]
            
        # Update image if provided
        if "profile_image" in request.FILES:
            user.profile_image = request.FILES["profile_image"]
            
        user.save()
        
        return Response({
            "message": "Profile updated successfully",
            "email": user.email,
            "full_name": user.full_name,
            "mobile": user.mobile,
            "profile_image": user.profile_image.url if user.profile_image else None
        }, status=status.HTTP_200_OK)



from rest_framework.views import APIView
from rest_framework.response import Response
from florra_admin.models import Product
from .serializers import ProductSerializer

class ProductListView(APIView):
    def get(self, request):
        category = request.GET.get("category")

        products = Product.objects.all()
        if category:
            products = products.filter(category=category)

        serializer = ProductSerializer(products, many=True, context={'request': request})
        return Response(serializer.data)


from django.db.models import Q
from rest_framework.views import APIView
from rest_framework.response import Response
from florra_admin.models import Product
from .serializers import ProductSerializer

class ProductSearchView(APIView):
    def get(self, request):
        q = request.GET.get("q", "").strip()

        if not q:
            return Response([])

        products = Product.objects.filter(
            Q(tile_name__icontains=q) |
            Q(brand_name__icontains=q) |
            Q(category__icontains=q) |
            Q(size__icontains=q) |
            Q(finish__icontains=q) |
            Q(color__icontains=q)
        )

        serializer = ProductSerializer(products, many=True, context={'request': request})
        return Response(serializer.data)




class ProductDetailView(APIView):
    def get(self, request, pk):
        product = Product.objects.get(id=pk)
        serializer = ProductSerializer(product, context={'request': request})
        return Response(serializer.data)


from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import Quotation
from .serializers import QuotationSerializer


class QuotationCreateView(APIView):
    def post(self, request):
        serializer = QuotationSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(
                {"message": "Quotation created successfully"},
                status=status.HTTP_201_CREATED
            )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import Quotation
from .serializers import QuotationSerializer
from django.shortcuts import get_object_or_404


class QuotationDetailView(APIView):

    def get(self, request, quotation_id):
        quotation = get_object_or_404(Quotation, quotation_id=quotation_id)
        serializer = QuotationSerializer(quotation)
        return Response(serializer.data)

    def patch(self, request, quotation_id):
        quotation = get_object_or_404(Quotation, quotation_id=quotation_id)
        serializer = QuotationSerializer(
            quotation, data=request.data, partial=True
        )
        if serializer.is_valid():
            serializer.save()
            return Response(
                {"message": "Quotation updated successfully"},
                status=status.HTTP_200_OK
            )
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.shortcuts import get_object_or_404

from .models import Favorite
from florra_admin.models import Product
from .serializers import FavoriteSerializer


class FavoriteListCreateView(APIView):
    permission_classes = [IsAuthenticated]
    
    def get(self, request):
        favorites = Favorite.objects.filter(user=request.user)
        serializer = FavoriteSerializer(favorites, many=True, context={'request': request})
        return Response(serializer.data)

    def post(self, request):
        product_id = request.data.get("product_id")
        if not product_id:
            return Response(
                {"error": "product_id is required"},
                status=status.HTTP_400_BAD_REQUEST
            )

        product = get_object_or_404(Product, id=product_id)

        favorite, created = Favorite.objects.get_or_create(
            user=request.user,
            product=product
        )

        if not created:
            return Response(
                {"message": "Already in favorites"},
                status=status.HTTP_200_OK
            )

        return Response(
            {"message": "Added to favorites"},
            status=status.HTTP_201_CREATED
        )


class FavoriteDeleteView(APIView):
    permission_classes = [IsAuthenticated]

    def delete(self, request, product_id):

        favorite = get_object_or_404(
            Favorite,
            user=request.user,
            product_id=product_id
        )
        favorite.delete()

        return Response(
            {"message": "Removed from favorites"},
            status=status.HTTP_200_OK
        )


from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import IsAuthenticated

from .models import Notification
from .serializers import NotificationSerializer


class NotificationListView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        notif_type = request.GET.get("type")  # quotation / system

        qs = Notification.objects.filter(user=request.user)

        if notif_type in ["quotation", "system"]:
            qs = qs.filter(notification_type=notif_type)

        serializer = NotificationSerializer(
            qs.order_by("-created_at"),
            many=True
        )
        return Response(serializer.data)


class MarkAllReadView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        Notification.objects.filter(
            user=request.user,
            is_read=False
        ).update(is_read=True)

        return Response(
            {"message": "All notifications marked as read"},
            status=status.HTTP_200_OK
        )


from rest_framework.views import APIView
from rest_framework.parsers import MultiPartParser
from rest_framework.response import Response
from rest_framework import status

from .models import Product
from .serializers import ProductSerializer


from .ai.matcher import find_similar_tiles
from rest_framework.parsers import MultiPartParser, FormParser

class AIScanView(APIView):
    """
    Vision-based AI Recommendation (ResNet50).
    Input: Image File
    Output: Ranked list of products (Exact Match + Related)
    """
    parser_classes = [MultiPartParser, FormParser]
    permission_classes = [AllowAny] 

    def post(self, request):
        import logging
        logging.basicConfig(
            filename='debug_matching.log',
            filemode='w',
            format='%(asctime)s - %(message)s',
            level=logging.INFO
        )
        logging.info("--- New Scan Request ---")

        if 'image' not in request.FILES:
            return Response({"error": "No image provided"}, status=status.HTTP_400_BAD_REQUEST)

        image_file = request.FILES.get('image')
        
        # 1. Run AI Matcher
        try:
            results = find_similar_tiles(image_file, top_k=6)
            logging.info(f"Matcher returned {len(results)} results")
        except Exception as e:
            logging.error(f"Matcher failed: {e}")
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        if not results:
            logging.warning("No vectors found in index or no matches.")
            return Response({"message": "No matching tiles found"}, status=status.HTTP_404_NOT_FOUND)

        # 2. Fetch Product Details from DB
        response_data = []
        
        # Debug: Check if we can see Product ID 6
        try:
            p6 = Product.objects.get(id=6)
            logging.info(f"DEBUG CHECK: Product ID 6 exists: '{p6.tile_name}', Image: '{p6.image}'")
        except Exception as e:
            logging.error(f"DEBUG CHECK: Product ID 6 lookup FAILED: {e}")

        for filename, score in results:
            filename = str(filename).strip() # Sanitize
            
            # Hex Debug
            try:
                fname_hex = filename.encode('utf-8').hex()
                logging.info(f"Processing result: filename='{filename}' (Hex: {fname_hex}), score={score}")
            except:
                logging.info(f"Processing result: filename='{filename}', score={score}")

            # Try exact/partial match
            # Explicitly cast to string just in case
            product = Product.objects.filter(image__icontains=str(filename)).first()
            
            if product:
                logging.info(f"  -> Found match: ID={product.id}, Name='{product.tile_name}'")
            else:
                 logging.warning(f"  -> NO MATCH in DB for '{filename}'")
                 # Check what is actually in DB for similar names?
                 # Debug: try to list some close matches?

            # Fallback: Try matching without extension
            if not product and "." in filename:
                name_only = filename.rsplit(".", 1)[0]
                if len(name_only) > 3:
                    product = Product.objects.filter(image__icontains=name_only).first()
                    if product:
                        logging.info(f"  -> Found match (fallback): ID={product.id}, Name='{product.tile_name}'")

            if product:
                data = ProductSerializer(product, context={'request': request}).data
                data['similarity_score'] = round(float(score), 3)
                
                if score > 0.92:
                    data['match_type'] = "EXACT MATCH"
                elif score > 0.75:
                    data['match_type'] = "SIMILAR"
                else:
                    data['match_type'] = "RELATED"
                
                response_data.append(data)

        if not response_data:
            logging.warning("All matcher results failed DB lookup.")
            return Response({"message": "No matching tiles found"}, status=status.HTTP_404_NOT_FOUND)

        return Response(response_data)



class CustomerChatView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        user_message = request.data.get("message", "").lower()
        
        # Basic logic - can be expanded with real AI/NLP later
        if any(word in user_message for word in ["hi", "hello", "hey"]):
            reply = "Hello! I'm your Florra Assistant. I can help you find the perfect tiles, check prices, or track your quotes. How can I assist you today?"
        
        elif "price" in user_message or "cost" in user_message:
            from .models import Product
            products = Product.objects.all()[:3]
            reply = "Our prices vary by material and finish. For example:\n"
            for p in products:
                reply += f"• {p.name}: ${p.price_per_sq_ft} per sq ft\n"
            reply += "\nYou can see all prices in our catalog!"

        elif "stock" in user_message or "available" in user_message:
            reply = "Most of our premium collection is currently in stock. If you have a specific tile in mind (like 'Carrara' or 'Slate'), let me know and I'll check the exact availability for you."

        elif "recommend" in user_message or "suggest" in user_message or "best" in user_message:
            reply = "Based on current trends, I recommend our **Carrara White Marble** for a classic look or **Charcoal Slate** for a modern industrial feel. Would you like to see them in the catalog?"

        elif "quote" in user_message or "quotation" in user_message:
            reply = "You can request a personalized quotation by clicking the 'Request Quote' button on any product page. Once requested, our team will get back to you within 24 hours."

        elif "contact" in user_message or "support" in user_message:
            reply = "You can reach our support team at support@florra.com or call us at +1-800-FLORRA."

        elif "thank" in user_message:
            reply = "You're very welcome! Is there anything else I can help you with?"

        else:
            reply = "That's interesting! I'm still learning, but I can certainly help you with tile selection, pricing, or quotations. Try asking 'What are your best marble tiles?'"

        return Response({"reply": reply})
