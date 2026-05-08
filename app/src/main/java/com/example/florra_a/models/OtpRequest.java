package com.example.florra_a.models;

public class OtpRequest {
    private String email;
    private String purpose;

    public OtpRequest(String email, String purpose) {
        this.email = email;
        this.purpose = purpose;
    }

    public String getEmail() { return email; }
    public String getPurpose() { return purpose; }
}
