package com.example.florra_a;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    // Base URL - Change to your local IP for testing on physical device
    String BASE_URL = "http://10.0.2.2/florra_api/"; // For emulator
    // String BASE_URL = "http://192.168.1.100/florra_api/"; // For physical device (your PC's IP)

    @POST("api/auth/login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("api/auth/register.php")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("api/auth/forgot_password.php")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);
}