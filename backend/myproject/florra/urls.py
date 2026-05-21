from django.urls import path
from .views import (
    SendOTPView, ResetPasswordOTPView,
    CustomerRegisterView,
    CustomerLoginView,
    GoogleLoginView,
    CustomerChangePasswordView,
    CustomerUpdateProfileView,
    ProductListView,
    ProductSearchView,
    QuotationCreateView,
    ProductDetailView,
    QuotationDetailView,
    FavoriteListCreateView,
    FavoriteDeleteView,
    NotificationListView,
    MarkAllReadView,
    AIScanView,
    CustomerChatView
)

urlpatterns = [
    path("customer/send-otp/", SendOTPView.as_view()),
    path("customer/reset-password-otp/", ResetPasswordOTPView.as_view()),
    path("customer/register/", CustomerRegisterView.as_view()),
    path("customer/login/", CustomerLoginView.as_view()),
    path("customer/google-login/", GoogleLoginView.as_view()),
    path("customer/change-password/", CustomerChangePasswordView.as_view()),
    path("customer/update-profile/", CustomerUpdateProfileView.as_view()),
    path("products/", ProductListView.as_view()),
    path("products/search/", ProductSearchView.as_view()),
    path("products/<int:pk>/", ProductDetailView.as_view()),
    path("quotations/", QuotationCreateView.as_view(), name="quotation-create"),
    path("quotations/<str:quotation_id>/", QuotationDetailView.as_view()),
    path("favorites/", FavoriteListCreateView.as_view()),
    path("favorites/<int:product_id>/", FavoriteDeleteView.as_view()),
    path("notifications/", NotificationListView.as_view()),
    path("notifications/mark-all-read/", MarkAllReadView.as_view()),
    path("ai/scan-tile/", AIScanView.as_view()),
    path("customer/chat/", CustomerChatView.as_view()),
    
    # Moved from florra_admin
    path("customer/search-image/", AIScanView.as_view()), # Using existing AIScanView
    path("customer/notifications/", NotificationListView.as_view()), # Using existing NotificationListView
]
