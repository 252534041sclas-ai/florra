package com.example.florra_a;

public class Tile {
    private String name;
    private String price;
    private String size;  // Just dimensions like "60×60"
    private String stock;
    private int image;
    private String stockType;

    public Tile(String name, String price, String size, String stock, int image, String stockType) {
        this.name = name;
        this.price = price;
        this.size = size;
        this.stock = stock;
        this.image = image;
        this.stockType = stockType;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getSize() {
        return size;  // This returns just the dimensions
    }

    public String getStock() {
        return stock;
    }

    public int getImage() {
        return image;
    }

    public String getStockType() {
        return stockType;
    }

    // Optional: Method to get finish type based on name
    public String getFinish() {
        String nameLower = name.toLowerCase();
        if (nameLower.contains("matte") || nameLower.contains("nero")) {
            return "Matte";
        } else if (nameLower.contains("rustic") || nameLower.contains("travertine")) {
            return "Rustic";
        } else if (nameLower.contains("high gloss") || nameLower.contains("statuario")) {
            return "High Gloss";
        } else if (nameLower.contains("wood")) {
            return "Wood Finish";
        } else {
            return "Glossy"; // default
        }
    }
}