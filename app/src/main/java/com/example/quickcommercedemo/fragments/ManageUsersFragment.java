package com.example.quickcommercedemo.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.adapters.UserManagementAdapter;
import com.example.quickcommercedemo.dialogs.EditUserDialogFragment;
import com.example.quickcommercedemo.models.User;
import com.example.quickcommercedemo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;

    private UserRepository userRepository;
    private List<User> allUsers = new ArrayList<>();
    private UserManagementAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);

        userRepository = new UserRepository();

        rvUsers = view.findViewById(R.id.rvManageUsers);
        progressBar = view.findViewById(R.id.progressBarUsers);
        tvEmpty = view.findViewById(R.id.tvEmptyUsers);
        etSearch = view.findViewById(R.id.etSearchUser);

        setupRecyclerView();
        setupSearch();
        loadUsers();

        return view;
    }

    private void setupRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserManagementAdapter(new ArrayList<>(), new UserManagementAdapter.OnUserActionListener() {
            @Override
            public void onEditUser(User user) {
                showEditUserDialog(user);
            }

            @Override
            public void onToggleBan(User user) {
                toggleBanStatus(user);
            }
        });
        rvUsers.setAdapter(adapter);
    }

    private void showEditUserDialog(User user) {
        EditUserDialogFragment dialog = EditUserDialogFragment.newInstance(user, () -> {
            loadUsers(); // Refresh list after update
            Toast.makeText(requireContext(), "User profile updated successfully", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getChildFragmentManager(), "EditUserDialog");
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
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                allUsers = users;
                filter(etSearch.getText().toString());
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "User status updated", Toast.LENGTH_SHORT).show();
                loadUsers(); // Refresh list
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
