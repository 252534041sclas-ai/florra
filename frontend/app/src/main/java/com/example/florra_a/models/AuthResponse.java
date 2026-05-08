package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private String token;
    private String email;
    
    @SerializedName("full_name")
    private String fullName;
    
    @SerializedName("user_type")
    private String userType;

    @SerializedName("profile_image")
    private String profileImage;

    // Optional error message field
    private String message;

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getUserType() { return userType; }
    public String getProfileImage() { return profileImage; }
    public String getMessage() { return message; }
}
