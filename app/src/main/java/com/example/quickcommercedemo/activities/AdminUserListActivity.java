package com.example.quickcommercedemo.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.adapters.UserManagementAdapter;
import com.example.quickcommercedemo.models.User;
import com.example.quickcommercedemo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminUserListActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;
    private Toolbar toolbar;

    private UserRepository userRepository;
    private List<User> allUsers = new ArrayList<>();
    private UserManagementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        userRepository = new UserRepository();

        initViews();
        setupRecyclerView();
        setupSearch();
        loadUsers();
    }

    private void initViews() {
        rvUsers = findViewById(R.id.rvAdminUsers);
        progressBar = findViewById(R.id.progressBarUsers);
        tvEmpty = findViewById(R.id.tvEmptyUsers);
        etSearch = findViewById(R.id.etSearchUser);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Users");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserManagementAdapter(new ArrayList<>(), new UserManagementAdapter.OnUserActionListener() {
            @Override
            public void onEditUser(User user) {
                // Edit logic
            }

            @Override
            public void onToggleBan(User user) {
                toggleBanStatus(user);
            }
        });
        rvUsers.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(List<User> users) {
                progressBar.setVisibility(View.GONE);
                allUsers = users;
                filter(etSearch.getText().toString());
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminUserListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String query) {
        List<User> filtered;
        if (query.isEmpty()) {
            filtered = new ArrayList<>(allUsers);
        } else {
            String q = query.toLowerCase();
            filtered = allUsers.stream()
                    .filter(u -> u.getName().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        adapter.updateList(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void toggleBanStatus(User user) {
        boolean newStatus = !user.isBanned();
        userRepository.toggleBanStatus(user.getUserId(), newStatus, new UserRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminUserListActivity.this, "User status updated", Toast.LENGTH_SHORT).show();
                loadUsers();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminUserListActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
