package com.example.florra_a.models;

import java.io.Serializable;

public class CustomerProfile implements Serializable {
    private String fullName;
    private String email;
    private String mobileNumber;

    public CustomerProfile(String fullName, String email, String mobileNumber) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
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
    
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
