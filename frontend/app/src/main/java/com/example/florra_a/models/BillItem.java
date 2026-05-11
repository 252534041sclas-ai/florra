package com.example.florra_a.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BillItem implements Serializable {
    @SerializedName("item_name")
    private String item_name;
    
    @SerializedName("size")
    private String size;
    
    @SerializedName("quantity")
    private int quantity;
    
    @SerializedName("rate")
    private double rate;
    
    @SerializedName("amount")
    private double amount;

    @SerializedName("tile_no")
    private String tileNo;

    // Optional: product_id if we want to link it back, but simple text is fine for now
    
    public BillItem() {}

    public BillItem(String item_name, String tileNo, String size, int quantity, double rate, double amount) {
        this.item_name = item_name;
        this.tileNo = tileNo;
        this.size = size;
        this.quantity = quantity;
        this.rate = rate;
        this.amount = amount;
    }

    public String getItemName() { return item_name; }
    public void setItemName(String item_name) { this.item_name = item_name; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTileNo() { return tileNo; }
    public void setTileNo(String tileNo) { this.tileNo = tileNo; }
}
