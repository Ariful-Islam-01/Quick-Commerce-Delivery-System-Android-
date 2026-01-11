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
        void onEditClick(Order order);
        void onCancelClick(Order order);
        void onMainActionClick(Order order);
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
        holder.bind(orders.get(position), isAvailableOrders, listener);
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

        public void bind(Order order, boolean isAvailableOrders, OnOrderClickListener listener) {
            tvProductName.setText(order.getProductName());
            tvStatus.setText(order.getStatus());
            tvDescriptionSnippet.setText(order.getDescription() != null ? order.getDescription() : "");
            tvTimeRange.setText("Time: " + order.getTimeFrom() + " - " + order.getTimeTo());
            tvDeliveryFee.setText(String.format("৳%.2f", order.getDeliveryFee()));

            if (isAvailableOrders) {
                // View for Available Deliveries
                btnView.setVisibility(View.GONE);
                btnEdit.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnMainAction.setVisibility(View.VISIBLE);
                btnMainAction.setText("Accept Delivery");
            } else {
                // View for My Orders
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
