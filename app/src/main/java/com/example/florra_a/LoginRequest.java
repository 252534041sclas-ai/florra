package com.example.florra_a;

public class LoginRequest {
    private String email;
    private String password;
    private String user_type;

    public LoginRequest(String email, String password, String user_type) {
        this.email = email;
        this.password = password;
        this.user_type = user_type;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}