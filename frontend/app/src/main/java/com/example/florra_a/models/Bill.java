package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Bill implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("bill_no")
    private String bill_no;
    
    @SerializedName("customer_name")
    private String customer_name;
    
    @SerializedName("customer_phone")
    private String customer_phone;
    
    @SerializedName("customer_address")
    private String customer_address;
    
    @SerializedName("subtotal")
    private double subtotal;
    
    @SerializedName("gst_percentage")
    private double gst_percentage;
    
    @SerializedName("gst_amount")
    private double gst_amount;
    
    @SerializedName("discount")
    private double discount;
    
    @SerializedName("loading")
    private double loading;
    
    @SerializedName("grand_total")
    private double grand_total;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String created_at;
    
    @SerializedName("items")
    private java.util.List<BillItem> items;

    // Constructors
    public Bill() {}

    public Bill(String bill_no, String customer_name, String customer_phone, String customer_address, 
                double subtotal, double gst_percentage, double gst_amount, double discount, 
                double loading, double grand_total, String status) {
        this.bill_no = bill_no;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_address = customer_address;
        this.subtotal = subtotal;
        this.gst_percentage = gst_percentage;
        this.gst_amount = gst_amount;
        this.discount = discount;
        this.loading = loading;
        this.grand_total = grand_total;
        this.status = status;
        this.items = new java.util.ArrayList<>();
    }

    public Bill(String bill_no, String customer_name, String customer_phone, String customer_address, 
                double subtotal, double gst_percentage, double gst_amount, double discount, 
                double loading, double grand_total, String status, java.util.List<BillItem> items) {
        this(bill_no, customer_name, customer_phone, customer_address, subtotal, gst_percentage, gst_amount, discount, loading, grand_total, status);
        this.items = items;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBillNo() { return bill_no; }
    public void setBillNo(String bill_no) { this.bill_no = bill_no; }

    public String getCustomerName() { return customer_name; }
    public void setCustomerName(String customer_name) { this.customer_name = customer_name; }

    public String getCustomerPhone() { return customer_phone; }
    public void setCustomerPhone(String customer_phone) { this.customer_phone = customer_phone; }

    public String getCustomerAddress() { return customer_address; }
    public void setCustomerAddress(String customer_address) { this.customer_address = customer_address; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getGstPercentage() { return gst_percentage; }
    public void setGstPercentage(double gst_percentage) { this.gst_percentage = gst_percentage; }

    public double getGstAmount() { return gst_amount; }
    public void setGstAmount(double gst_amount) { this.gst_amount = gst_amount; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getLoading() { return loading; }
    public void setLoading(double loading) { this.loading = loading; }

    public double getGrandTotal() { return grand_total; }
    public void setGrandTotal(double grand_total) { this.grand_total = grand_total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return created_at; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }

    public java.util.List<BillItem> getItems() { return items; }
    public void setItems(java.util.List<BillItem> items) { this.items = items; }
}
