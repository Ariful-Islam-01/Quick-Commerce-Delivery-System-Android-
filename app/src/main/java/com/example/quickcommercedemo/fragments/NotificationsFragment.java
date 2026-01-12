package com.example.quickcommercedemo.fragments;

import android.content.Intent;
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
import com.example.quickcommercedemo.activities.OrderDetailsActivity;
import com.example.quickcommercedemo.adapters.NotificationAdapter;
import com.example.quickcommercedemo.models.Notification;
import com.example.quickcommercedemo.repositories.NotificationRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    
    private NotificationRepository notificationRepository;
    private SessionManager sessionManager;
    private NotificationAdapter adapter;
    private ValueEventListener notificationListener;

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
        startListeningForNotifications();

        return view;
    }

    private void setupRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(new ArrayList<>(), notification -> {
            if (!notification.isRead()) {
                markAsRead(notification);
            }
            
            // Navigate to related order if applicable
            if (notification.getRelatedId() != null && !notification.getRelatedId().isEmpty()) {
                Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
                intent.putExtra("orderId", notification.getRelatedId());
                startActivity(intent);
            }
        });
        rvNotifications.setAdapter(adapter);
    }

    private void startListeningForNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        notificationListener = notificationRepository.listenToNotificationsByUserId(userId, new NotificationRepository.NotificationsCallback() {
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
        notificationRepository.markAsRead(notification.getNotificationId(), new NotificationRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                // UI will auto-update via the listener
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to update notification", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notificationListener != null) {
            notificationRepository.removeListener(notificationListener);
        }
    }
}
