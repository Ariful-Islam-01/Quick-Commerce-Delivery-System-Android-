package com.example.quickcommercedemo.fragments;

import android.content.Intent;
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
import com.example.quickcommercedemo.activities.OrderDetailsActivity;
import com.example.quickcommercedemo.adapters.OrderCardAdapter;
import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AvailableOrdersFragment extends Fragment {

    private RecyclerView rvAvailableOrders;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;

    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private OrderCardAdapter adapter;
    private List<Order> allAvailableOrders = new ArrayList<>();
    private ValueEventListener availableListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_orders, container, false);

        orderRepository = new OrderRepository();
        sessionManager = new SessionManager(requireContext());

        rvAvailableOrders = view.findViewById(R.id.rvAvailableOrders);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        etSearch = view.findViewById(R.id.etSearchAvailable);

        setupRecyclerView();
        setupSearch();
        startListeningForAvailableOrders();

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

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void startListeningForAvailableOrders() {
        progressBar.setVisibility(View.VISIBLE);
        String currentUserId = sessionManager.getUserId();

        availableListener = orderRepository.listenToPendingOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                // Filter: Only show pending orders from OTHER users
                allAvailableOrders = orders.stream()
                        .filter(o -> !o.getCustomerId().equals(currentUserId))
                        .collect(Collectors.toList());
                
                filter(etSearch.getText().toString());
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void filter(String query) {
        List<Order> filtered;
        if (query.isEmpty()) {
            filtered = new ArrayList<>(allAvailableOrders);
        } else {
            String q = query.toLowerCase();
            filtered = allAvailableOrders.stream()
                    .filter(o -> o.getProductName().toLowerCase().contains(q) || o.getLocation().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        adapter.updateList(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void acceptOrder(Order order) {
        String userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        orderRepository.acceptOrder(order.getOrderId(), userId, userName, new OrderRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                if (isAdded()) Toast.makeText(requireContext(), "Delivery accepted!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Exception e) {
                if (isAdded()) Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (availableListener != null) orderRepository.removeListener(availableListener);
    }
}
