package com.example.quickcommercedemo.models;

public class Order {
    private String orderId;
    private String customerId;
    private String customerName;
    private String category;
    private String productName;
    private String description;
    private String location;
    private double latitude;
    private double longitude;
    private String deliveryDate;
    private String timeFrom;
    private String timeTo;
    private double deliveryFee;
    private String status; // Pending, Accepted, Picked Up, Delivered, Cancelled
    private String acceptedByUserId;
    private String acceptedByName;
    private long createdAt;
    private long updatedAt;

    public Order() {
        // Required empty constructor for Firebase
    }

    public Order(String customerId, String customerName, String category, String productName,
                 String description, String location, double latitude, double longitude,
                 String deliveryDate, String timeFrom, String timeTo, double deliveryFee) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.category = category;
        this.productName = productName;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryDate = deliveryDate;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.deliveryFee = deliveryFee;
        this.status = "Pending";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getTimeFrom() { return timeFrom; }
    public void setTimeFrom(String timeFrom) { this.timeFrom = timeFrom; }

    public String getTimeTo() { return timeTo; }
    public void setTimeTo(String timeTo) { this.timeTo = timeTo; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAcceptedByUserId() { return acceptedByUserId; }
    public void setAcceptedByUserId(String acceptedByUserId) { this.acceptedByUserId = acceptedByUserId; }

    public String getAcceptedByName() { return acceptedByName; }
    public void setAcceptedByName(String acceptedByName) { this.acceptedByName = acceptedByName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
