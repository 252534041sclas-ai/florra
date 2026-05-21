package com.example.florra_a.models;

public class GoogleLoginRequest {
    private String email;
    private String full_name;

    public GoogleLoginRequest(String email, String full_name) {
        this.email = email;
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }
}
