from django.contrib import admin

# Register your models here.
from django.contrib import admin
from .models import Quotation

admin.site.register(Quotation)

from django.contrib import admin
from .models import Product

admin.site.register(Product)
class ProductAdmin(admin.ModelAdmin):
    list_display = (
        'tile_name',
        'brand_name',
        'category',
        'color',
        'finish',
        'price',
        'stock',
        'is_active'
    )
