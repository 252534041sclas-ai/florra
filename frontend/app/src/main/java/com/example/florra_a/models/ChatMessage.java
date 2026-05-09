package com.example.florra_a.models;

import java.io.Serializable;

import java.util.List;

public class ChatMessage implements Serializable {
    private String message;
    private boolean isUser;
    private String imagePath;
    private List<Product> products;
    private boolean isTyping;
    private long timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    public static ChatMessage createTypingIndicator() {
        ChatMessage msg = new ChatMessage("", false);
        msg.setTyping(true);
        return msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
