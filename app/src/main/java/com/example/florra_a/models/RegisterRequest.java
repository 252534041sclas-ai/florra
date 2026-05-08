package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("full_name")
    private String fullName;
    private String email;
    private String mobile;
    private String password;
    private String otp;

    public RegisterRequest(String fullName, String email, String mobile, String password, String otp) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.otp = otp;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getPassword() { return password; }
    public String getOtp() { return otp; }
}
