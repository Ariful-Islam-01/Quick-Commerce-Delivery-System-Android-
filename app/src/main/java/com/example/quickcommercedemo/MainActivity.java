package com.example.quickcommercedemo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.quickcommercedemo.activities.AdminMainActivity;
import com.example.quickcommercedemo.activities.LoginActivity;
import com.example.quickcommercedemo.fragments.DeliveriesFragment;
import com.example.quickcommercedemo.fragments.EarningsFragment;
import com.example.quickcommercedemo.fragments.HomeFragment;
import com.example.quickcommercedemo.fragments.MyOrdersFragment;
import com.example.quickcommercedemo.fragments.ProfileFragment;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (sessionManager.isAdmin()) {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
            return;
        }

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new MyOrdersFragment();
            } else if (itemId == R.id.nav_deliveries) {
                selectedFragment = new DeliveriesFragment();
            } else if (itemId == R.id.nav_earnings) {
                selectedFragment = new EarningsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }
    
    public void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit();
    }
}
