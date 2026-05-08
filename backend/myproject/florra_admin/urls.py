from django.urls import path
from .views import *

urlpatterns = [
    path('login/', AdminLoginView.as_view(), name='admin-login'),
    path('products/', ProductListView.as_view()),
    path('products/add/', ProductCreateView.as_view()),
    path('products/<int:pk>/', ProductDetailView.as_view()),
    path('bills/', BillCreateView.as_view()),
    path('bills/list/', BillListView.as_view()),
    path('bills/<int:pk>/', BillDetailView.as_view()),
    path('bills/<int:pk>/status/', BillStatusUpdateView.as_view()),
    path('enquiries/', EnquiryListView.as_view()),
    path('enquiries/create/', EnquiryCreateView.as_view()),
    path('enquiries/<int:pk>/update/', EnquiryStatusUpdateView.as_view()),
    path('enquiries/respond/', EnquiryRespondView.as_view()),
    path('api/customer/search-image/', ImageSearchProductView.as_view()),
    path('api/customer/notifications/', CustomerNotificationView.as_view()),

    path('sales-prediction/', SalesPredictionView.as_view()),
    path("inventory/", InventoryView.as_view()),
]
