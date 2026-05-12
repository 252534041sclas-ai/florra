package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ReportAnalyticsResponse {
    @SerializedName("revenue_chart")
    private List<Double> revenueChart;

    @SerializedName("quotation_distribution")
    private Map<String, Integer> quotationDistribution;

    @SerializedName("best_sellers")
    private List<BestSeller> bestSellers;

    public List<Double> getRevenueChart() { return revenueChart; }
    public Map<String, Integer> getQuotationDistribution() { return quotationDistribution; }
    public List<BestSeller> getBestSellers() { return bestSellers; }

    public static class BestSeller {
        @SerializedName("name")
        private String name;
        @SerializedName("quantity")
        private int quantity;

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
    }
}
