package com.example.florra_a;

public class Tile {
    private String name;
    private String price;
    private String size;
    private String stockStatus;
    private int imageResource;
    private String finish;
    private String category;

    public Tile(String name, String price, String size, String stockStatus,
                int imageResource, String finish, String category) {
        this.name = name;
        this.price = price;
        this.size = size;
        this.stockStatus = stockStatus;
        this.imageResource = imageResource;
        this.finish = finish;
        this.category = category;
    }

    // Getters
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getSize() { return size; }
    public String getStockStatus() { return stockStatus; }
    public int getImageResource() { return imageResource; }
    public String getFinish() { return finish; }
    public String getCategory() { return category; }

    // Setters (optional)
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setSize(String size) { this.size = size; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }
    public void setFinish(String finish) { this.finish = finish; }
    public void setCategory(String category) { this.category = category; }
}