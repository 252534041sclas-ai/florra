package com.example.florra_a.models;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Enquiry implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("customer_name")
    private String customer_name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;
    @SerializedName("reference")
    private String reference;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("customer_email")
    private String customer_email;
    
    // Quotation Details
    private String quotation_price;
    private String quotation_boxes;
    private String quotation_delivery_time;
    private String quotation_notes;

    // Getters
    public int getId() { return id; }
    public String getCustomerName() { return customer_name; }
    public String getPhone() { return phone; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getReference() { return reference; }
    public String getCreatedAt() { return created_at; }
    public String getCustomerEmail() { return customer_email; }
    
    public String getQuotationPrice() { return quotation_price; }
    public String getQuotationBoxes() { return quotation_boxes; }
    public String getQuotationDeliveryTime() { return quotation_delivery_time; }
    public String getQuotationNotes() { return quotation_notes; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCustomerName(String customer_name) { this.customer_name = customer_name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMessage(String message) { this.message = message; }
    public void setStatus(String status) { this.status = status; }
    public void setReference(String reference) { this.reference = reference; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }
    public void setCustomerEmail(String customer_email) { this.customer_email = customer_email; }

    public void setQuotationPrice(String quotation_price) { this.quotation_price = quotation_price; }
    public void setQuotationBoxes(String quotation_boxes) { this.quotation_boxes = quotation_boxes; }
    public void setQuotationDeliveryTime(String quotation_delivery_time) { this.quotation_delivery_time = quotation_delivery_time; }
    public void setQuotationNotes(String quotation_notes) { this.quotation_notes = quotation_notes; }
}
