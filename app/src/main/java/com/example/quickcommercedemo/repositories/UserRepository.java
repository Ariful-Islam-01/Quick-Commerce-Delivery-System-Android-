package com.example.quickcommercedemo.repositories;

import androidx.annotation.NonNull;

import com.example.quickcommercedemo.models.User;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final DatabaseReference usersRef;

    public UserRepository() {
        this.usersRef = DatabaseManager.getInstance().getUsersReference();
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onFailure(Exception e);
    }

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Create user
    public void createUser(User user, VoidCallback callback) {
        usersRef.child(user.getUserId())
            .setValue(user)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }

    // Get user by ID
    public void getUserById(String userId, UserCallback callback) {
        usersRef.child(userId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure(new Exception("Failed to parse user data"));
                        }
                    } else {
                        callback.onFailure(new Exception("User not found"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onFailure(error.toException());
                }
            });
    }

    // Update user
    public void updateUser(User user, VoidCallback callback) {
        usersRef.child(user.getUserId())
            .setValue(user)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }

    // Get all users (Admin)
    public void getAllUsers(UsersCallback callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    // Ban/Unban user
    public void toggleBanStatus(String userId, boolean isBanned, VoidCallback callback) {
        usersRef.child(userId).child("banned")
            .setValue(isBanned)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }

    // Delete user
    public void deleteUser(String userId, VoidCallback callback) {
        usersRef.child(userId)
            .removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }


    // Update rating
    public void updateUserRating(String userId, double averageRating, int totalRatings, VoidCallback callback) {
        usersRef.child(userId)
            .child("averageRating").setValue(averageRating);
        usersRef.child(userId)
            .child("totalRatings").setValue(totalRatings)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }
}

