package com.example.quickcommercedemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    public void updateList(List<Notification> newList) {
        this.notifications = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(notifications.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private View vUnreadIndicator;
        private TextView tvTitle, tvMessage, tvTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            vUnreadIndicator = itemView.findViewById(R.id.vUnreadIndicator);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
        }

        public void bind(Notification notification, OnNotificationClickListener listener) {
            tvTitle.setText(notification.getTitle());
            tvMessage.setText(notification.getMessage());
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            tvTime.setText(sdf.format(new Date(notification.getCreatedAt())));
            
            vUnreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);
            
            itemView.setOnClickListener(v -> listener.onNotificationClick(notification));
        }
    }
}
