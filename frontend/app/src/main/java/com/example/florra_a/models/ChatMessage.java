package com.example.florra_a.models;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_AI = 1;

    private String message;
    private int type;
    private String time;

    public ChatMessage(String message, int type, String time) {
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public String getTime() {
        return time;
    }
}
