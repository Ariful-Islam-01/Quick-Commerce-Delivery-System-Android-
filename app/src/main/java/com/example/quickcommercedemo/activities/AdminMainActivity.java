package com.example.quickcommercedemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.fragments.ManageEarningsFragment;
import com.example.quickcommercedemo.fragments.ManageOrdersFragment;
import com.example.quickcommercedemo.fragments.ManageUsersFragment;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.repositories.UserRepository;
import com.example.quickcommercedemo.utils.SessionManager;

public class AdminMainActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvTotalOrders, tvTotalEarnings;
    private CardView cardManageUsers, cardManageOrders, cardReports, cardLogout;
    private View mainAdminContent;
    
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
        mainAdminContent = findViewById(R.id.mainAdminContent);
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
        cardManageUsers.setOnClickListener(v -> loadAdminFragment(new ManageUsersFragment(), "Manage Users"));
        cardManageOrders.setOnClickListener(v -> loadAdminFragment(new ManageOrdersFragment(), "Manage Orders"));
        cardReports.setOnClickListener(v -> loadAdminFragment(new ManageEarningsFragment(), "System Reports"));

        cardLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadAdminFragment(Fragment fragment, String title) {
        findViewById(R.id.adminFragmentContainer).setVisibility(View.VISIBLE);
        mainAdminContent.setVisibility(View.GONE);
        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            mainAdminContent.setVisibility(View.VISIBLE);
            findViewById(R.id.adminFragmentContainer).setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
