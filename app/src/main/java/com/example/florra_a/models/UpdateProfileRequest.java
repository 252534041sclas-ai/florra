package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("full_name")
    private String fullName;

    private String email;
    private String mobile;

    public UpdateProfileRequest(String fullName, String email, String mobile) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
