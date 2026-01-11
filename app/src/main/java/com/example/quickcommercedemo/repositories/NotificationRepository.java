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
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("Failed to generate notification ID"));
        }
    }

    public void getNotificationsByUserId(String userId, NotificationsCallback callback) {
        notificationsRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
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
            });
    }
}

