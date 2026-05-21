package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Notification implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("type")
    private String type; // "SYSTEM", "QUOTATION", etc.
    
    @SerializedName("timestamp")
    private String timestamp;
    
    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("product_id")
    private Integer productId;

    @SerializedName("product_image")
    private String productImage;

    // Constructors
    public Notification() {}

    public Notification(String title, String message, String type, String timestamp, boolean isRead) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
}
