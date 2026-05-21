from rest_framework import serializers
from .models import CustomerUser


class CustomerRegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)

    class Meta:
        model = CustomerUser
        fields = ["full_name", "email", "mobile", "password"]

    def create(self, validated_data):
        return CustomerUser.objects.create_user(
            email=validated_data["email"],
            password=validated_data["password"],
            full_name=validated_data["full_name"],
            mobile=validated_data.get("mobile", "")
        )


class CustomerLoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField()

from rest_framework import serializers
from florra_admin.models import Product

class ProductSerializer(serializers.ModelSerializer):
    is_favorite = serializers.SerializerMethodField()
    image = serializers.SerializerMethodField()
    thickness = serializers.ReadOnlyField()
    coverage = serializers.ReadOnlyField()

    class Meta:
        model = Product
        fields = "__all__"

    def get_image(self, obj):
        if not obj.image:
            return None
        return obj.image.name

    def get_is_favorite(self, obj):
        request = self.context.get("request")
        if request and request.user.is_authenticated:
            # Import here to avoid circular import if necessary
            from .models import Favorite
            return Favorite.objects.filter(user=request.user, product=obj).exists()
        return False


from rest_framework import serializers
from .models import Quotation


class QuotationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Quotation
        fields = "__all__"


from rest_framework import serializers
from .models import Favorite


class FavoriteSerializer(serializers.ModelSerializer):
    # Flatten product fields to match Product model structure for Android
    id = serializers.IntegerField(source="product.id", read_only=True)
    tile_name = serializers.CharField(source="product.tile_name", read_only=True)
    brand_name = serializers.CharField(source="product.brand_name", read_only=True)
    category = serializers.CharField(source="product.category", read_only=True)
    size = serializers.CharField(source="product.size", read_only=True)
    finish = serializers.CharField(source="product.finish", read_only=True)
    color = serializers.CharField(source="product.color", read_only=True)
    price = serializers.CharField(source="product.price", read_only=True)
    stock = serializers.IntegerField(source="product.stock", read_only=True)
    description = serializers.CharField(source="product.description", read_only=True)
    image = serializers.ImageField(source="product.image", read_only=True)
    is_favorite = serializers.BooleanField(default=True, read_only=True)
    thickness = serializers.CharField(source="product.thickness", read_only=True)
    coverage = serializers.CharField(source="product.coverage", read_only=True)

    class Meta:
        model = Favorite
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
            "description",
            "image",
            "is_favorite",
            "thickness",
            "coverage",
        ]


from rest_framework import serializers
from .models import Notification


class NotificationSerializer(serializers.ModelSerializer):
    time_ago = serializers.SerializerMethodField()
    product_id = serializers.SerializerMethodField()
    product_image = serializers.SerializerMethodField()

    class Meta:
        model = Notification
        fields = [
            "id",
            "title",
            "message",
            "notification_type",
            "is_read",
            "time_ago",
            "product_id",
            "product_image",
        ]

    def get_time_ago(self, obj):
        from django.utils.timesince import timesince
        return timesince(obj.created_at) + " ago"

    def get_product_id(self, obj):
        return obj.product.id if obj.product else None

    def get_product_image(self, obj):
        if obj.product and obj.product.image:
            return obj.product.image.url
        return None


