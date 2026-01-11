package com.example.quickcommercedemo.repositories;

import androidx.annotation.NonNull;

import com.example.quickcommercedemo.models.Rating;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RatingRepository {

    private final DatabaseReference ratingsRef;

    public RatingRepository() {
        this.ratingsRef = DatabaseManager.getInstance().getRatingsReference();
    }

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void submitRating(Rating rating, VoidCallback callback) {
        String ratingId = ratingsRef.push().getKey();
        if (ratingId != null) {
            rating.setRatingId(ratingId);
            ratingsRef.child(ratingId)
                .setValue(rating)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("Failed to generate rating ID"));
        }
    }

    public interface AverageRatingCallback {
        void onSuccess(double averageRating, int totalRatings);
        void onFailure(Exception e);
    }

    public void getAverageRating(String deliveryPersonId, AverageRatingCallback callback) {
        ratingsRef.orderByChild("deliveryPersonId").equalTo(deliveryPersonId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    float total = 0;

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Rating rating = childSnapshot.getValue(Rating.class);
                        if (rating != null) {
                            total += rating.getRating();
                            count++;
                        }
                    }

                    if (count == 0) {
                        callback.onSuccess(0.0, 0);
                    } else {
                        double average = total / count;
                        callback.onSuccess(average, count);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(error.toException());
                }
            });
    }
}

