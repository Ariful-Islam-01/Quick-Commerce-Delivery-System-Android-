package com.example.quickcommercedemo.models;

public class OrderFormItem {
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
    private String photoUri;

    public OrderFormItem() {
        this.category = "Groceries";
        this.deliveryFee = 0.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    // Getters and Setters
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

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }

    public boolean isValid() {
        return productName != null && !productName.trim().isEmpty()
                && location != null && !location.trim().isEmpty()
                && deliveryDate != null && !deliveryDate.trim().isEmpty()
                && timeFrom != null && !timeFrom.trim().isEmpty()
                && timeTo != null && !timeTo.trim().isEmpty()
                && deliveryFee > 0;
    }
}
