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

public class OrderCardAdapter extends RecyclerView.Adapter<OrderCardAdapter.OrderViewHolder> {

    private List<Order> orders;
    private final OnOrderClickListener listener;
    private final boolean isAvailableOrders;
    private final boolean isMyTask;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onEditClick(Order order);
        void onCancelClick(Order order);
        void onMainActionClick(Order order);
    }

    public OrderCardAdapter(List<Order> orders, OnOrderClickListener listener) {
        this(orders, false, false, listener);
    }

    public OrderCardAdapter(List<Order> orders, boolean isAvailableOrders, OnOrderClickListener listener) {
        this(orders, isAvailableOrders, false, listener);
    }

    public OrderCardAdapter(List<Order> orders, boolean isAvailableOrders, boolean isMyTask, OnOrderClickListener listener) {
        this.orders = orders;
        this.isAvailableOrders = isAvailableOrders;
        this.isMyTask = isMyTask;
        this.listener = listener;
    }

    public void updateList(List<Order> newList) {
        this.orders = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position), isAvailableOrders, isMyTask, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName, tvStatus, tvDescriptionSnippet, tvTimeRange, tvDeliveryFee;
        private final MaterialButton btnView, btnEdit, btnCancel, btnMainAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescriptionSnippet = itemView.findViewById(R.id.tvDescriptionSnippet);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvDeliveryFee = itemView.findViewById(R.id.tvDeliveryFee);
            
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnMainAction = itemView.findViewById(R.id.btnMainAction);
        }

        public void bind(Order order, boolean isAvailableOrders, boolean isMyTask, OnOrderClickListener listener) {
            tvProductName.setText(order.getProductName());
            tvStatus.setText(order.getStatus());
            tvDescriptionSnippet.setText(order.getDescription() != null ? order.getDescription() : "");
            tvTimeRange.setText("Time: " + order.getTimeFrom() + " - " + order.getTimeTo());
            tvDeliveryFee.setText(String.format("৳%.2f", order.getDeliveryFee()));

            // Apply Status-Specific Colors
            int statusColor;
            switch (order.getStatus()) {
                case "Pending": statusColor = Color.parseColor("#FFC107"); break;
                case "Accepted": statusColor = Color.parseColor("#2196F3"); break;
                case "Picked Up": statusColor = Color.parseColor("#9C27B0"); break;
                case "On the Way": statusColor = Color.parseColor("#673AB7"); break;
                case "Delivered": statusColor = Color.parseColor("#4CAF50"); break;
                case "Cancelled": statusColor = Color.parseColor("#F44336"); break;
                default: statusColor = Color.GRAY;
            }
            tvStatus.setTextColor(statusColor);

            if (isAvailableOrders) {
                btnView.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnMainAction.setVisibility(View.VISIBLE);
                btnMainAction.setText("Accept Delivery");
            } else if (isMyTask) {
                btnView.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                
                boolean isActive = !"Delivered".equals(order.getStatus()) && !"Cancelled".equals(order.getStatus());
                btnMainAction.setVisibility(isActive ? View.VISIBLE : View.GONE);
                btnMainAction.setText("Update Status");
            } else {
                // My Requested Orders
                btnView.setVisibility(View.VISIBLE);
                boolean isPending = "Pending".equals(order.getStatus());
                btnEdit.setVisibility(isPending ? View.VISIBLE : View.GONE);
                btnCancel.setVisibility(isPending ? View.VISIBLE : View.GONE);
                btnMainAction.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onOrderClick(order));
            btnView.setOnClickListener(v -> listener.onOrderClick(order));
            btnEdit.setOnClickListener(v -> listener.onEditClick(order));
            btnCancel.setOnClickListener(v -> listener.onCancelClick(order));
            btnMainAction.setOnClickListener(v -> listener.onMainActionClick(order));
        }
    }
}
