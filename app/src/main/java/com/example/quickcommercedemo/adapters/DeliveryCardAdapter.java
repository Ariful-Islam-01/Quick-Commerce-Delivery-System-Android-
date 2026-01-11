package com.example.quickcommercedemo.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.Order;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DeliveryCardAdapter extends RecyclerView.Adapter<DeliveryCardAdapter.DeliveryViewHolder> {

    private List<Order> deliveries;
    private final OnDeliveryClickListener listener;

    public interface OnDeliveryClickListener {
        void onDeliveryClick(Order order);
        void onUpdateStatusClick(Order order);
    }

    public DeliveryCardAdapter(List<Order> deliveries, OnDeliveryClickListener listener) {
        this.deliveries = deliveries;
        this.listener = listener;
    }

    public void updateList(List<Order> newList) {
        this.deliveries = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        holder.bind(deliveries.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName, tvStatus, tvDescriptionSnippet, tvTimeRange, tvDeliveryFee;
        private final MaterialButton btnView, btnMainAction;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescriptionSnippet = itemView.findViewById(R.id.tvDescriptionSnippet);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvDeliveryFee = itemView.findViewById(R.id.tvDeliveryFee);
            
            btnView = itemView.findViewById(R.id.btnView);
            btnMainAction = itemView.findViewById(R.id.btnMainAction);
            
            // Hide customer-only buttons
            itemView.findViewById(R.id.btnEdit).setVisibility(View.GONE);
            itemView.findViewById(R.id.btnCancel).setVisibility(View.GONE);
        }

        public void bind(Order order, OnDeliveryClickListener listener) {
            tvProductName.setText(order.getProductName());
            tvStatus.setText(order.getStatus());
            tvDescriptionSnippet.setText(order.getDescription() != null ? order.getDescription() : "");
            tvTimeRange.setText("Time: " + order.getTimeFrom() + " - " + order.getTimeTo());
            tvDeliveryFee.setText(String.format("৳%.2f", order.getDeliveryFee()));

            int statusColor;
            switch (order.getStatus()) {
                case "Accepted": statusColor = Color.parseColor("#2196F3"); break;
                case "Picked Up": statusColor = Color.parseColor("#9C27B0"); break;
                case "On the Way": statusColor = Color.parseColor("#673AB7"); break;
                case "Delivered": statusColor = Color.parseColor("#4CAF50"); break;
                default: statusColor = Color.GRAY;
            }
            tvStatus.setTextColor(statusColor);

            btnView.setVisibility(View.VISIBLE);
            boolean isActive = !"Delivered".equals(order.getStatus());
            btnMainAction.setVisibility(isActive ? View.VISIBLE : View.GONE);
            btnMainAction.setText("Update Status");

            itemView.setOnClickListener(v -> listener.onDeliveryClick(order));
            btnView.setOnClickListener(v -> listener.onDeliveryClick(order));
            btnMainAction.setOnClickListener(v -> listener.onUpdateStatusClick(order));
        }
    }
}
