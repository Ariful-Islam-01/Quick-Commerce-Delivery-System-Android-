package com.example.quickcommercedemo.models;

public class Rating {
    private String ratingId;
    private String orderId;
    private String customerId;
    private String deliveryPersonId;
    private float rating; // 1-5 stars
    private String comment;
    private long createdAt;

    public Rating() {
        // Required empty constructor for Firebase
    }

    public Rating(String orderId, String customerId, String deliveryPersonId,
                  float rating, String comment) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.deliveryPersonId = deliveryPersonId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getRatingId() { return ratingId; }
    public void setRatingId(String ratingId) { this.ratingId = ratingId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(String deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

