package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;

public class ReportSummaryResponse {
    @SerializedName("summary")
    private Summary summary;

    @SerializedName("insights")
    private Insights insights;

    public Summary getSummary() { return summary; }
    public Insights getInsights() { return insights; }

    public static class Summary {
        @SerializedName("total_enquiries")
        private int totalEnquiries;
        @SerializedName("total_quotations")
        private int totalQuotations;
        @SerializedName("confirmed_orders")
        private int confirmedOrders;
        @SerializedName("pending_quotations")
        private int pendingQuotations;
        @SerializedName("cancelled_quotations")
        private int cancelledQuotations;
        @SerializedName("total_revenue")
        private double totalRevenue;
        @SerializedName("profit")
        private double profit;
        @SerializedName("conversion_percentage")
        private double conversionPercentage;

        public int getTotalEnquiries() { return totalEnquiries; }
        public int getTotalQuotations() { return totalQuotations; }
        public int getConfirmedOrders() { return confirmedOrders; }
        public int getPendingQuotations() { return pendingQuotations; }
        public int getCancelledQuotations() { return cancelledQuotations; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getProfit() { return profit; }
        public double getConversionPercentage() { return conversionPercentage; }
    }

    public static class Insights {
        @SerializedName("growth_percentage")
        private double growthPercentage;
        @SerializedName("ai_insight")
        private String aiInsight;

        public double getGrowthPercentage() { return growthPercentage; }
        public String getAiInsight() { return aiInsight; }
    }
}
