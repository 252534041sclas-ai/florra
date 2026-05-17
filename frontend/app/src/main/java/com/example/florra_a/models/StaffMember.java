package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;

public class StaffMember {
    private int id;
    
    @SerializedName("full_name")
    private String fullName;
    
    private String email;
    private String password;
    private String role; // "admin" or "staff"

    @SerializedName("can_access_billing")
    private boolean canAccessBilling;

    @SerializedName("can_access_reports")
    private boolean canAccessReports;

    @SerializedName("can_access_predictions")
    private boolean canAccessPredictions;

    public StaffMember() {}

    public StaffMember(String fullName, String email, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public StaffMember(String fullName, String email, String password, String role, boolean canAccessBilling, boolean canAccessReports) {
        this(fullName, email, password, role, canAccessBilling, canAccessReports, false);
    }

    public StaffMember(String fullName, String email, String password, String role, boolean canAccessBilling, boolean canAccessReports, boolean canAccessPredictions) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.canAccessBilling = canAccessBilling;
        this.canAccessReports = canAccessReports;
        this.canAccessPredictions = canAccessPredictions;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isCanAccessBilling() { return canAccessBilling; }
    public void setCanAccessBilling(boolean canAccessBilling) { this.canAccessBilling = canAccessBilling; }

    public boolean isCanAccessReports() { return canAccessReports; }
    public void setCanAccessReports(boolean canAccessReports) { this.canAccessReports = canAccessReports; }

    public boolean isCanAccessPredictions() { return canAccessPredictions; }
    public void setCanAccessPredictions(boolean canAccessPredictions) { this.canAccessPredictions = canAccessPredictions; }
}
