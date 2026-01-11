package com.example.quickcommercedemo.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String defaultAddress;
    private boolean isAdmin;
    private boolean isBanned;
    private long createdAt;
    private double latitude;
    private double longitude;
    private double averageRating;
    private int totalRatings;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String userId, String name, String email, String phone, String defaultAddress) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.defaultAddress = defaultAddress;
        this.isAdmin = false;
        this.isBanned = false;
        this.createdAt = System.currentTimeMillis();
        this.averageRating = 0.0;
        this.totalRatings = 0;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDefaultAddress() { return defaultAddress; }
    public void setDefaultAddress(String defaultAddress) { this.defaultAddress = defaultAddress; }


    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
}

