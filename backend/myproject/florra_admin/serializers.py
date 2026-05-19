from rest_framework import serializers

class AdminLoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField()


from .models import Product

class ProductSerializer(serializers.ModelSerializer):
    is_favorite = serializers.SerializerMethodField()
    class Meta:
        model = Product
        fields = '__all__'

    def get_is_favorite(self, obj):
        request = self.context.get("request")
        if request and request.user.is_authenticated:
            from florra.models import Favorite
            return Favorite.objects.filter(user=request.user, product=obj).exists()
        return False

from rest_framework import serializers
from .models import Bill, BillItem

class BillItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = BillItem
        exclude = ['bill']


class BillSerializer(serializers.ModelSerializer):
    items = BillItemSerializer(many=True, required=False)
    customer_image = serializers.SerializerMethodField()

    class Meta:
        model = Bill
        fields = "__all__"

    def get_customer_image(self, obj):
        from florra.models import CustomerUser
        if obj.customer_phone:
            try:
                user = CustomerUser.objects.filter(mobile=obj.customer_phone).first()
                if user and user.profile_image:
                    request = self.context.get('request')
                    if request:
                        return request.build_absolute_uri(user.profile_image.url)
                    return user.profile_image.url
            except Exception:
                pass
        return None

    def create(self, validated_data):
        items_data = validated_data.pop('items', [])
        bill = Bill.objects.create(**validated_data)

        for item in items_data:
            BillItem.objects.create(bill=bill, **item)

        return bill


from rest_framework import serializers
from .models import Enquiry

class EnquirySerializer(serializers.ModelSerializer):
    customer_image = serializers.SerializerMethodField()

    class Meta:
        model = Enquiry
        fields = "__all__"

    def get_customer_image(self, obj):
        from florra.models import CustomerUser
        user = None
        if obj.customer_email:
            user = CustomerUser.objects.filter(email=obj.customer_email).first()
        if not user and obj.phone:
            user = CustomerUser.objects.filter(mobile=obj.phone).first()
        
        if user and user.profile_image:
            request = self.context.get('request')
            if request:
                return request.build_absolute_uri(user.profile_image.url)
            return user.profile_image.url
        return None


from rest_framework import serializers
from .models import Product


class InventoryProductSerializer(serializers.ModelSerializer):
    stock_status = serializers.SerializerMethodField()
    is_favorite = serializers.SerializerMethodField()

    class Meta:
        model = Product
        fields = [
            "id",
            "tile_name",
            "brand_name",
            "category",
            "size",
            "finish",
            "color",
            "price",
            "stock",
            "stock_status",
            "image",
            "is_favorite",
        ]

    def get_is_favorite(self, obj):
        request = self.context.get("request")
        if request and request.user.is_authenticated:
            from florra.models import Favorite
            return Favorite.objects.filter(user=request.user, product=obj).exists()
        return False


    def get_stock_status(self, obj):
        if obj.stock == 0:
            return "Empty"
        elif obj.stock < 50:
            return "Low Stock"
        return "In Stock"

from .models import AdminUser

class AdminUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = AdminUser
        fields = ['id', 'full_name', 'email', 'role', 'is_active', 'can_access_billing', 'can_access_reports', 'can_access_predictions', 'mobile', 'profile_image', 'created_at']

