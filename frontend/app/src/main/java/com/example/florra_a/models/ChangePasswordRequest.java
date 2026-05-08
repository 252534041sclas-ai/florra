package com.example.florra_a.models;

public class ChangePasswordRequest {
    private String email;
    private String old_password;
    private String new_password;

    public ChangePasswordRequest(String email, String old_password, String new_password) {
        this.email = email;
        this.old_password = old_password;
        this.new_password = new_password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOldPassword() {
        return old_password;
    }

    public void setOldPassword(String old_password) {
        this.old_password = old_password;
    }

    public String getNewPassword() {
        return new_password;
    }

    public void setNewPassword(String new_password) {
        this.new_password = new_password;
    }
}
