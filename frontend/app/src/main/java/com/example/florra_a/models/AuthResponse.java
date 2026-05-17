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

    private String role;
    private String message;

    @SerializedName("can_access_billing")
    private boolean canAccessBilling;

    @SerializedName("can_access_reports")
    private boolean canAccessReports;

    @SerializedName("can_access_predictions")
    private boolean canAccessPredictions;

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getUserType() { return userType; }
    public String getProfileImage() { return profileImage; }
    public String getMessage() { return message; }
    public String getRole() { return role; }
    public boolean isCanAccessBilling() { return canAccessBilling; }
    public boolean isCanAccessReports() { return canAccessReports; }
    public boolean isCanAccessPredictions() { return canAccessPredictions; }
}
