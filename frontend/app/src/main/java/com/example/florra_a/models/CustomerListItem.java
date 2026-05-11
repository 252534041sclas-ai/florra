package com.example.florra_a.models;

import java.io.Serializable;

public class CustomerListItem implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String address;
    private int billCount;
    private int enquiryCount;

    public CustomerListItem(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getBillCount() { return billCount; }
    public void setBillCount(int billCount) { this.billCount = billCount; }

    public int getEnquiryCount() { return enquiryCount; }
    public void setEnquiryCount(int enquiryCount) { this.enquiryCount = enquiryCount; }
}
