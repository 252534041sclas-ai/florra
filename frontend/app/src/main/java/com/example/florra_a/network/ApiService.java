package com.example.florra_a.network;

import com.example.florra_a.models.AuthResponse;
import com.example.florra_a.models.LoginRequest;
import com.example.florra_a.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/customer/send-otp/")
    Call<okhttp3.ResponseBody> sendOtp(@Body com.example.florra_a.models.OtpRequest request);

    @POST("api/customer/reset-password-otp/")
    Call<okhttp3.ResponseBody> resetPasswordWithOtp(@Body com.example.florra_a.models.ResetPasswordRequest request);

    @POST("api/customer/register/")
    Call<AuthResponse> registerCustomer(@Body RegisterRequest request);

    @POST("api/customer/login/")
    Call<AuthResponse> loginCustomer(@Body LoginRequest request);

    @POST("api/admin/login/")
    Call<AuthResponse> loginAdmin(@Body LoginRequest request);

    @POST("api/customer/change-password/")
    Call<okhttp3.ResponseBody> changePassword(@Body com.example.florra_a.models.ChangePasswordRequest request);

    @retrofit2.http.Multipart
    @POST("api/customer/update-profile/")
    Call<AuthResponse> updateProfile(
            @retrofit2.http.Part("full_name") okhttp3.RequestBody fullName,
            @retrofit2.http.Part("mobile") okhttp3.RequestBody mobile,
            @retrofit2.http.Part okhttp3.MultipartBody.Part profileImage
    );

    @retrofit2.http.Multipart
    @retrofit2.http.POST("api/admin/products/add/")
    Call<com.example.florra_a.models.Product> addProduct(
        @retrofit2.http.PartMap java.util.Map<String, okhttp3.RequestBody> textFields,
        @retrofit2.http.Part okhttp3.MultipartBody.Part image
    );

    @retrofit2.http.Multipart
    @retrofit2.http.PATCH("api/admin/products/{id}/")
    Call<com.example.florra_a.models.Product> updateProduct(
        @retrofit2.http.Path("id") int id,
        @retrofit2.http.PartMap java.util.Map<String, okhttp3.RequestBody> textFields,
        @retrofit2.http.Part okhttp3.MultipartBody.Part image
    );

    @retrofit2.http.GET("api/admin/products/")
    Call<java.util.List<com.example.florra_a.models.Product>> getProducts();

    // === BILL ENDPOINTS ===
    @retrofit2.http.POST("api/admin/bills/")
    Call<okhttp3.ResponseBody> saveBill(@Body com.example.florra_a.models.Bill bill);

    @retrofit2.http.GET("api/admin/bills/list/")
    Call<java.util.List<com.example.florra_a.models.Bill>> getBills();

    @retrofit2.http.GET("api/admin/sales-prediction/")
    Call<com.example.florra_a.models.SalesPredictionResponse> getSalesPrediction();

    @retrofit2.http.POST("api/admin/enquiries/create/")
    Call<com.example.florra_a.models.Enquiry> createEnquiry(@Body com.example.florra_a.models.Enquiry enquiry);

    @retrofit2.http.POST("api/admin/enquiries/respond/")
    Call<com.example.florra_a.models.Enquiry> respondToEnquiry(@Body com.example.florra_a.models.Enquiry enquiry);

    @retrofit2.http.GET("api/admin/enquiries/")
    Call<java.util.List<com.example.florra_a.models.Enquiry>> getEnquiries();

    @retrofit2.http.GET("api/admin/inventory/")
    Call<com.example.florra_a.models.InventoryResponse> getInventory(
        @retrofit2.http.Query("search") String search,
        @retrofit2.http.Query("category") String category,
        @retrofit2.http.Query("finish") String finish
    );
    @retrofit2.http.GET("api/customer/notifications/")
    Call<java.util.List<com.example.florra_a.models.Notification>> getNotifications();

    @retrofit2.http.Multipart
    @retrofit2.http.POST("api/ai/scan-tile/")
    Call<java.util.List<com.example.florra_a.models.Product>> searchByImage(
        @retrofit2.http.Part okhttp3.MultipartBody.Part image
    );

    @retrofit2.http.Multipart
    @retrofit2.http.POST("api/ai/scan-tile/")
    Call<okhttp3.ResponseBody> searchByImageRaw(
        @retrofit2.http.Part okhttp3.MultipartBody.Part image
    );

    // Favorites
    @retrofit2.http.POST("api/favorites/")
    Call<okhttp3.ResponseBody> addToFavorites(@Body java.util.Map<String, Integer> body);

    @retrofit2.http.GET("api/favorites/")
    Call<java.util.List<com.example.florra_a.models.Product>> getFavorites();

    @retrofit2.http.DELETE("api/favorites/{id}/")
    Call<okhttp3.ResponseBody> removeFromFavorites(@retrofit2.http.Path("id") int id);
}
