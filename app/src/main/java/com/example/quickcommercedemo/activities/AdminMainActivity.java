package com.example.quickcommercedemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.repositories.UserRepository;
import com.example.quickcommercedemo.utils.SessionManager;

public class AdminMainActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalOrders, tvTotalEarnings;
    private CardView cardManageUsers, cardManageOrders, cardReports, cardLogout;
    
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        userRepository = new UserRepository();
        orderRepository = new OrderRepository();
        sessionManager = new SessionManager(this);

        initViews();
        loadStats();
        setupListeners();
    }

    private void initViews() {
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalEarnings = findViewById(R.id.tvTotalEarnings);

        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardManageOrders = findViewById(R.id.cardManageOrders);
        cardReports = findViewById(R.id.cardReports);
        cardLogout = findViewById(R.id.cardLogout);
    }

    private void loadStats() {
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.quickcommercedemo.models.User> users) {
                tvTotalUsers.setText(String.valueOf(users.size()));
            }
            @Override
            public void onFailure(Exception e) {}
        });

        orderRepository.getAllOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.quickcommercedemo.models.Order> orders) {
                tvTotalOrders.setText(String.valueOf(orders.size()));
                double total = orders.stream()
                        .filter(o -> "Delivered".equals(o.getStatus()))
                        .mapToDouble(com.example.quickcommercedemo.models.Order::getDeliveryFee)
                        .sum();
                tvTotalEarnings.setText(String.format("৳%.2f", total));
            }
            @Override
            public void onFailure(Exception e) {}
        });
    }

    private void setupListeners() {
        cardManageUsers.setOnClickListener(v -> 
            startActivity(new Intent(this, AdminUserListActivity.class)));
            
        cardManageOrders.setOnClickListener(v -> 
            Toast.makeText(this, "Order Management coming soon", Toast.LENGTH_SHORT).show());

        cardLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
