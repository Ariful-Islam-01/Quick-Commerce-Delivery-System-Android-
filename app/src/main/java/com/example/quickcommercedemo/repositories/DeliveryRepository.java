package com.example.quickcommercedemo.repositories;

import androidx.annotation.NonNull;

import com.example.quickcommercedemo.models.Delivery;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryRepository {

    private final DatabaseReference deliveriesRef;

    public DeliveryRepository() {
        this.deliveriesRef = DatabaseManager.getInstance().getDeliveriesReference();
    }

    public interface DeliveriesCallback {
        void onSuccess(List<Delivery> deliveries);
        void onFailure(Exception e);
    }

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void createDelivery(Delivery delivery, VoidCallback callback) {
        String deliveryId = deliveriesRef.push().getKey();
        if (deliveryId != null) {
            delivery.setDeliveryId(deliveryId);
            deliveriesRef.child(deliveryId)
                .setValue(delivery)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("Failed to generate delivery ID"));
        }
    }

    public void getDeliveriesByDeliveryPerson(String deliveryPersonId, DeliveriesCallback callback) {
        deliveriesRef.orderByChild("deliveryPersonId").equalTo(deliveryPersonId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Delivery> deliveries = new ArrayList<>();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Delivery delivery = childSnapshot.getValue(Delivery.class);
                        if (delivery != null) {
                            deliveries.add(delivery);
                        }
                    }
                    deliveries.sort((d1, d2) -> Long.compare(d2.getAcceptedAt(), d1.getAcceptedAt()));
                    callback.onSuccess(deliveries);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(error.toException());
                }
            });
    }

    public void completeDelivery(String orderId, VoidCallback callback) {
        deliveriesRef.orderByChild("orderId").equalTo(orderId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String deliveryId = childSnapshot.getKey();
                            if (deliveryId != null) {
                                deliveriesRef.child(deliveryId).child("status").setValue("Completed");
                                deliveriesRef.child(deliveryId).child("completedAt").setValue(System.currentTimeMillis())
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(callback::onFailure);
                            }
                            return;
                        }
                    } else {
                        callback.onFailure(new Exception("Delivery not found"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(error.toException());
                }
            });
    }
}

