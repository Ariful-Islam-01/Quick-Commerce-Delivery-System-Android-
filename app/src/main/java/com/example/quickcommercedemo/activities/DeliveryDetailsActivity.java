package com.example.quickcommercedemo.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.example.quickcommercedemo.models.Order;

public class DeliveryDetailsActivity extends OrderDetailsActivity {
    // Inherit from OrderDetailsActivity since the details are identical
    // but we can add delivery-person specific logic here if needed.
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Additional setup for delivery view if necessary
    }
}
