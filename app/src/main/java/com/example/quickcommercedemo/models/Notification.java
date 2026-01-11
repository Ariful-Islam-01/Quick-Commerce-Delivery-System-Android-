package com.example.quickcommercedemo.models;

public class Notification {
    public enum NotificationType {
        INFO, SUCCESS, WARNING, ORDER_UPDATE, DELIVERY_UPDATE, EARNING
    }

    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private String type; // Store as string for Firebase
    private boolean isRead;
    private String relatedId; // orderId or deliveryId
    private long createdAt;

    public Notification() {
        // Required empty constructor for Firebase
    }

    public Notification(String userId, String title, String message,
                       NotificationType type, String relatedId) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type.name();
        this.relatedId = relatedId;
        this.isRead = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public NotificationType getTypeEnum() {
        try {
            return NotificationType.valueOf(type);
        } catch (Exception e) {
            return NotificationType.INFO;
        }
    }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getRelatedId() { return relatedId; }
    public void setRelatedId(String relatedId) { this.relatedId = relatedId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

