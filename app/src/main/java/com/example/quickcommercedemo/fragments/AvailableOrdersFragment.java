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
import com.example.quickcommercedemo.adapters.OrderCardAdapter;
import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AvailableOrdersFragment extends Fragment {

    private RecyclerView rvAvailableOrders;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private OrderCardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_orders, container, false);

        orderRepository = new OrderRepository();
        sessionManager = new SessionManager(requireContext());

        rvAvailableOrders = view.findViewById(R.id.rvAvailableOrders);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        setupRecyclerView();
        loadAvailableOrders();

        return view;
    }

    private void setupRecyclerView() {
        rvAvailableOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderCardAdapter(new ArrayList<>(), true, new OrderCardAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                startActivity(intent);
            }

            @Override
            public void onEditClick(Order order) {}

            @Override
            public void onCancelClick(Order order) {}

            @Override
            public void onMainActionClick(Order order) {
                acceptOrder(order);
            }
        });
        rvAvailableOrders.setAdapter(adapter);
    }

    private void loadAvailableOrders() {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = sessionManager.getUserId();

        orderRepository.getPendingOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                List<Order> otherUsersOrders = new ArrayList<>();
                for (Order o : orders) {
                    if (!o.getCustomerId().equals(currentUserId)) {
                        otherUsersOrders.add(o);
                    }
                }
                
                adapter.updateList(otherUsersOrders);
                tvEmpty.setVisibility(otherUsersOrders.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptOrder(Order order) {
        String userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        orderRepository.acceptOrder(order.getOrderId(), userId, userName, new OrderRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Order accepted!", Toast.LENGTH_SHORT).show();
                loadAvailableOrders();
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
