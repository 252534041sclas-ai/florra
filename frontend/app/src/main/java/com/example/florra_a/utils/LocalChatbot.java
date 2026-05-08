package com.example.florra_a.utils;

import java.util.HashMap;
import java.util.Map;

public class LocalChatbot {

    private static final Map<String, String> KNOWLEDGE_BASE = new HashMap<>();

    static {
        // Populate with data from customer_knowledge.txt
        KNOWLEDGE_BASE.put("quotation", "Customers can request quotations from the Request Quotation screen. Quotation details include price, quantity, and notes.");
        KNOWLEDGE_BASE.put("quote", "Customers can request quotations from the Request Quotation screen. Quotation details include price, quantity, and notes.");
        KNOWLEDGE_BASE.put("enquiry", "After submitting an enquiry, the admin reviews it and sends a quotation.");
        KNOWLEDGE_BASE.put("admin", "I cannot share admin or internal system information. I am a customer support assistant.");
        KNOWLEDGE_BASE.put("reply", "Customers can view admin replies in the Quotations screen.");
        KNOWLEDGE_BASE.put("view", "Customers can view admin replies in the Quotations screen.");
        KNOWLEDGE_BASE.put("hi", "Hello! How can I help you with your tiles and quotations today?");
        KNOWLEDGE_BASE.put("hello", "Hello! How can I help you with your tiles and quotations today?");
        KNOWLEDGE_BASE.put("help", "I can help you with questions about requesting quotations, viewing replies, and product details.");
    }

    public static String getResponse(String message) {
        String lowerMsg = message.toLowerCase();

        // Simple Keyword Matching
        for (Map.Entry<String, String> entry : KNOWLEDGE_BASE.entrySet()) {
            if (lowerMsg.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Default or "Thinking" response / Fallback
        return "I can help with quotations and enquiries. Please ask specifically about those topics.";
    }
}
