package com.example.florra_a.models;

import java.util.Map;

public class SalesPredictionResponse {
    private int predicted_sales;
    private double estimated_revenue;
    private double growth_percentage;
    private String high_demand_product;
    private String low_demand_product;
    private String high_demand_tile_no;
    private String low_demand_tile_no;
    private String stock_suggestion;
    private Map<String, Integer> chart;

    private java.util.List<MarketTrend> market_trends;
    private ActualData actual_data;

    public int getPredictedSales() { return predicted_sales; }
    public double getEstimatedRevenue() { return estimated_revenue; }
    public double getGrowthPercentage() { return growth_percentage; }
    public String getHighDemandProduct() { return high_demand_product; }
    public String getLowDemandProduct() { return low_demand_product; }
    public String getHighDemandTileNo() { return high_demand_tile_no; }
    public String getLowDemandTileNo() { return low_demand_tile_no; }
    public String getStockSuggestion() { return stock_suggestion; }
    public Map<String, Integer> getChart() { return chart; }
    public java.util.List<MarketTrend> getMarketTrends() { return market_trends; }
    public ActualData getActualData() { return actual_data; }

    public static class MarketTrend {
        private String name;
        private String value;
        private String trend; // "up" or "down"

        public String getName() { return name; }
        public String getValue() { return value; }
        public String getTrend() { return trend; }
    }

    public static class ActualData {
        private TimeRangeData this_month;
        private TimeRangeData last_month;
        private TimeRangeData last_3_months;
        private TimeRangeData yearly;

        public TimeRangeData getThisMonth() { return this_month; }
        public TimeRangeData getLastMonth() { return last_month; }
        public TimeRangeData getLast3Months() { return last_3_months; }
        public TimeRangeData getYearly() { return yearly; }
    }

    public static class TimeRangeData {
        private double sales;
        private double revenue;
        private String high_demand_product;
        private String low_demand_product;
        private String high_demand_tile_no;
        private String low_demand_tile_no;
        private java.util.List<Double> graph_data; // New

        public double getSales() { return sales; }
        public double getRevenue() { return revenue; }
        public String getHighDemandProduct() { return high_demand_product; }
        public String getLowDemandProduct() { return low_demand_product; }
        public String getHighDemandTileNo() { return high_demand_tile_no; }
        public String getLowDemandTileNo() { return low_demand_tile_no; }
        public java.util.List<Double> getGraphData() { return graph_data; }
    }
}
