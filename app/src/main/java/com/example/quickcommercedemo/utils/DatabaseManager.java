package com.example.quickcommercedemo.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;

    // Collection names
    public static final String USERS_COLLECTION = "users";
    public static final String ORDERS_COLLECTION = "orders";
    public static final String DELIVERIES_COLLECTION = "deliveries";
    public static final String EARNINGS_COLLECTION = "earnings";
    public static final String RATINGS_COLLECTION = "ratings";
    public static final String NOTIFICATIONS_COLLECTION = "notifications";

    private DatabaseManager() {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true); // Enable offline persistence
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getDatabaseReference() {
        return database.getReference();
    }

    public DatabaseReference getUsersReference() {
        return database.getReference(USERS_COLLECTION);
    }

    public DatabaseReference getOrdersReference() {
        return database.getReference(ORDERS_COLLECTION);
    }

    public DatabaseReference getDeliveriesReference() {
        return database.getReference(DELIVERIES_COLLECTION);
    }

    public DatabaseReference getEarningsReference() {
        return database.getReference(EARNINGS_COLLECTION);
    }

    public DatabaseReference getRatingsReference() {
        return database.getReference(RATINGS_COLLECTION);
    }

    public DatabaseReference getNotificationsReference() {
        return database.getReference(NOTIFICATIONS_COLLECTION);
    }

    public FirebaseAuth getAuth() {
        return auth;
    }


    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}

