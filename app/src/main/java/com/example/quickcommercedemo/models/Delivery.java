package com.example.quickcommercedemo.models;

public class Delivery {
    private String deliveryId;
    private String orderId;
    private String deliveryPersonId;
    private String deliveryPersonName;
    private String customerId;
    private String status; // Accepted, In Progress, Completed
    private long acceptedAt;
    private long completedAt;
    private double deliveryFee;

    public Delivery() {
        // Required empty constructor for Firebase
    }

    public Delivery(String orderId, String deliveryPersonId, String deliveryPersonName,
                    String customerId, double deliveryFee) {
        this.orderId = orderId;
        this.deliveryPersonId = deliveryPersonId;
        this.deliveryPersonName = deliveryPersonName;
        this.customerId = customerId;
        this.deliveryFee = deliveryFee;
        this.status = "Accepted";
        this.acceptedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(String deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public String getDeliveryPersonName() { return deliveryPersonName; }
    public void setDeliveryPersonName(String deliveryPersonName) { this.deliveryPersonName = deliveryPersonName; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(long acceptedAt) { this.acceptedAt = acceptedAt; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
}

