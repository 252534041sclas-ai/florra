package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;

public class AdminNotificationItem {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("sent_by")
    private String sentBy;

    @SerializedName("timestamp")
    private String timestamp;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public String getSentBy() { return sentBy; }
    public String getTimestamp() { return timestamp; }
}
