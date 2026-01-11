package com.example.quickcommercedemo.models;

public class Earning {
    private String earningId;
    private String deliveryPersonId;
    private String orderId;
    private String productName;
    private String location;
    private double amount;
    private long earnedAt;

    public Earning() {
        // Required empty constructor for Firebase
    }

    public Earning(String deliveryPersonId, String orderId, String productName,
                   String location, double amount) {
        this.deliveryPersonId = deliveryPersonId;
        this.orderId = orderId;
        this.productName = productName;
        this.location = location;
        this.amount = amount;
        this.earnedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getEarningId() { return earningId; }
    public void setEarningId(String earningId) { this.earningId = earningId; }

    public String getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(String deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public long getEarnedAt() { return earnedAt; }
    public void setEarnedAt(long earnedAt) { this.earnedAt = earnedAt; }
}

