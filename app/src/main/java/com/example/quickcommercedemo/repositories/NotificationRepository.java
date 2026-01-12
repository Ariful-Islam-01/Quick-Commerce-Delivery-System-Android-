package com.example.quickcommercedemo.repositories;

import androidx.annotation.NonNull;

import com.example.quickcommercedemo.models.Notification;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    private final DatabaseReference notificationsRef;

    public NotificationRepository() {
        this.notificationsRef = DatabaseManager.getInstance().getNotificationsReference();
    }

    public interface NotificationsCallback {
        void onSuccess(List<Notification> notifications);
        void onFailure(Exception e);
    }

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void createNotification(Notification notification, VoidCallback callback) {
        String notificationId = notificationsRef.push().getKey();
        if (notificationId != null) {
            notification.setNotificationId(notificationId);
            notificationsRef.child(notificationId)
                .setValue(notification)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
        } else {
            if (callback != null) callback.onFailure(new Exception("Failed to generate notification ID"));
        }
    }

    public ValueEventListener listenToNotificationsByUserId(String userId, NotificationsCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Notification notification = childSnapshot.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }
                notifications.sort((n1, n2) -> Long.compare(n2.getCreatedAt(), n1.getCreatedAt()));
                callback.onSuccess(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        };
        notificationsRef.orderByChild("userId").equalTo(userId).addValueEventListener(listener);
        return listener;
    }

    public void removeListener(ValueEventListener listener) {
        if (listener != null) {
            notificationsRef.removeEventListener(listener);
        }
    }

    public void markAsRead(String notificationId, VoidCallback callback) {
        notificationsRef.child(notificationId).child("read").setValue(true)
            .addOnSuccessListener(aVoid -> {
                if (callback != null) callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                if (callback != null) callback.onFailure(e);
            });
    }

    public void deleteNotification(String notificationId, VoidCallback callback) {
        notificationsRef.child(notificationId).removeValue()
            .addOnSuccessListener(aVoid -> {
                if (callback != null) callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                if (callback != null) callback.onFailure(e);
            });
    }
}
