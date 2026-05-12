package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReportCustomerResponse {
    @SerializedName("stats")
    private CustomerStats stats;

    @SerializedName("top_customers")
    private List<TopCustomer> topCustomers;

    public CustomerStats getStats() { return stats; }
    public List<TopCustomer> getTopCustomers() { return topCustomers; }

    public static class CustomerStats {
        @SerializedName("new_customers")
        private int newCustomers;
        @SerializedName("repeat_customers")
        private int repeatCustomers;
        @SerializedName("pending_followups")
        private int pendingFollowups;

        public int getNewCustomers() { return newCustomers; }
        public int getRepeatCustomers() { return repeatCustomers; }
        public int getPendingFollowups() { return pendingFollowups; }
    }

    public static class TopCustomer {
        @SerializedName("customer_name")
        private String customerName;
        @SerializedName("customer_phone")
        private String customerPhone;
        @SerializedName("total_orders")
        private int totalOrders;
        @SerializedName("total_amount")
        private double totalAmount;

        public String getCustomerName() { return customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public int getTotalOrders() { return totalOrders; }
        public double getTotalAmount() { return totalAmount; }
    }
}
