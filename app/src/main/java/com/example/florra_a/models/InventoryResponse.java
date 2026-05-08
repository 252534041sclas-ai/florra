package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class InventoryResponse implements Serializable {

    @SerializedName("stats")
    private InventoryStats stats;

    @SerializedName("products")
    private List<Product> products;

    public InventoryStats getStats() { return stats; }
    public List<Product> getProducts() { return products; }

    public static class InventoryStats implements Serializable {
        @SerializedName("total")
        private int total;

        @SerializedName("in_stock")
        private int inStock;

        @SerializedName("low_stock")
        private int lowStock;

        @SerializedName("empty")
        private int empty;

        public int getTotal() { return total; }
        public int getInStock() { return inStock; }
        public int getLowStock() { return lowStock; }
        public int getEmpty() { return empty; }
    }
}
