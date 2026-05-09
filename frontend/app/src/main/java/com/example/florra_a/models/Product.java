package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("tile_name")
    private String tileName;

    @SerializedName("tile_no")
    private String tileNo;

    @SerializedName("brand_name")
    private String brandName;

    @SerializedName("category")
    private String category;

    @SerializedName("size")
    private String size;

    @SerializedName("finish")
    private String finish;

    @SerializedName("color")
    private String color;

    @SerializedName("price")
    private String price;

    @SerializedName("stock")
    private int stock;

    @SerializedName("description")
    private String description;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("image")
    private String image;

    @SerializedName("stock_status")
    private String stockStatus;

    @SerializedName("thickness")
    private String thickness;

    @SerializedName("coverage")
    private String coverage;

    @SerializedName("warehouse")
    private String warehouse;

    @SerializedName("similarity_score")
    private double similarityScore;

    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }

    // Constructors
    public Product() {}

    public Product(String tileName, String brandName, String category, String size, 
                   String finish, String color, String price, int stock, 
                   String description, boolean isActive) {
        this.tileName = tileName;
        this.brandName = brandName;
        this.category = category;
        this.size = size;
        this.finish = finish;
        this.color = color;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public void setTileName(String tileName) { this.tileName = tileName; }

    public String getTileNo() { return tileNo; }
    public void setTileNo(String tileNo) { this.tileNo = tileNo; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getFinish() { return finish; }
    public void setFinish(String finish) { this.finish = finish; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public void setImage(String image) { this.image = image; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public String getThickness() { return thickness; }
    public void setThickness(String thickness) { this.thickness = thickness; }

    public String getCoverage() { return coverage; }
    public void setCoverage(String coverage) { this.coverage = coverage; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    // Favorites
    @SerializedName("is_favorite")
    private boolean isFavorite;
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    // Fallback fields for AI/Search Mismatches
    @SerializedName("name")
    private String name;

    @SerializedName("product_image")
    private String productImage;

    @SerializedName("title")
    private String title;

    @SerializedName("product_name")
    private String productName;

    // Updated Getters with Fallbacks
    public String getTileName() {
        if (tileName != null) return tileName;
        if (name != null) return name;
        if (title != null) return title;
        if (productName != null) return productName;
        return "";
    }

    public String getImage() {
        if (image != null) return image;
        if (productImage != null) return productImage;
        return ""; // Or null, adapter handles it
    }
}
