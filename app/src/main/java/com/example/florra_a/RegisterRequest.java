package com.example.florra_a;

public class RegisterRequest {
    private String full_name;
    private String email;
    private String mobile;
    private String password;
    private String confirm_password;

    public RegisterRequest(String full_name, String email, String mobile,
                           String password, String confirm_password) {
        this.full_name = full_name;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.confirm_password = confirm_password;
    }

    // Getters and Setters
    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }
}