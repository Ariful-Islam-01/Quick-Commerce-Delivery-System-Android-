package com.example.quickcommercedemo.repositories;

import androidx.annotation.NonNull;

import com.example.quickcommercedemo.models.Earning;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EarningRepository {

    private final DatabaseReference earningsRef;

    public EarningRepository() {
        this.earningsRef = DatabaseManager.getInstance().getEarningsReference();
    }

    public interface EarningsCallback {
        void onSuccess(List<Earning> earnings);
        void onFailure(Exception e);
    }

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void createEarning(Earning earning, VoidCallback callback) {
        String earningId = earningsRef.push().getKey();
        if (earningId != null) {
            earning.setEarningId(earningId);
            earningsRef.child(earningId)
                .setValue(earning)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("Failed to generate earning ID"));
        }
    }

    public void getEarningsByDeliveryPerson(String deliveryPersonId, EarningsCallback callback) {
        earningsRef.orderByChild("deliveryPersonId").equalTo(deliveryPersonId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Earning> earnings = new ArrayList<>();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Earning earning = childSnapshot.getValue(Earning.class);
                        if (earning != null) {
                            earnings.add(earning);
                        }
                    }
                    earnings.sort((e1, e2) -> Long.compare(e2.getEarnedAt(), e1.getEarnedAt()));
                    callback.onSuccess(earnings);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(error.toException());
                }
            });
    }
}

