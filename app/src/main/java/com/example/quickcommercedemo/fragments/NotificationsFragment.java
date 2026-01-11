package com.example.quickcommercedemo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.adapters.NotificationAdapter;
import com.example.quickcommercedemo.models.Notification;
import com.example.quickcommercedemo.repositories.NotificationRepository;
import com.example.quickcommercedemo.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    
    private NotificationRepository notificationRepository;
    private SessionManager sessionManager;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationRepository = new NotificationRepository();
        sessionManager = new SessionManager(requireContext());

        rvNotifications = view.findViewById(R.id.rvNotifications);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmptyNotifications);

        setupRecyclerView();
        loadNotifications();

        return view;
    }

    private void setupRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(new ArrayList<>(), notification -> {
            // Handle notification click (mark as read, navigate to related order, etc.)
            if (!notification.isRead()) {
                markAsRead(notification);
            }
        });
        rvNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        notificationRepository.getNotificationsByUserId(userId, new NotificationRepository.NotificationsCallback() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                adapter.updateList(notifications);
                tvEmpty.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsRead(Notification notification) {
        // Implementation for marking as read in repository would go here
        // For now just local update
        notification.setRead(true);
        adapter.notifyDataSetChanged();
    }
}
