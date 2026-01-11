package com.example.quickcommercedemo.adapters;

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

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onActionClick(Order order);
    }

    public OrderCardAdapter(List<Order> orders, OnOrderClickListener listener) {
        this(orders, false, listener);
    }

    public OrderCardAdapter(List<Order> orders, boolean isAvailableOrders, OnOrderClickListener listener) {
        this.orders = orders;
        this.isAvailableOrders = isAvailableOrders;
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
        Order order = orders.get(position);
        holder.bind(order, isAvailableOrders, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName, tvStatus, tvLocation, tvTimeRange, tvDeliveryFee;
        private final MaterialButton btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvDeliveryFee = itemView.findViewById(R.id.tvDeliveryFee);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(Order order, boolean isAvailableOrders, OnOrderClickListener listener) {
            tvProductName.setText(order.getProductName());
            tvStatus.setText(order.getStatus());
            tvLocation.setText(order.getLocation());
            tvTimeRange.setText("Time: " + order.getTimeFrom() + " - " + order.getTimeTo());
            tvDeliveryFee.setText(String.format("৳%.2f", order.getDeliveryFee()));

            itemView.setOnClickListener(v -> listener.onOrderClick(order));

            if (isAvailableOrders && "Pending".equals(order.getStatus())) {
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("Accept Delivery");
            } else {
                btnAction.setVisibility(View.GONE);
            }

            btnAction.setOnClickListener(v -> listener.onActionClick(order));
        }
    }
}
