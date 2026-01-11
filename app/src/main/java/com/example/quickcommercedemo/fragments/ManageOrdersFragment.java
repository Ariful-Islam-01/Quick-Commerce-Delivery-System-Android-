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
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageOrdersFragment extends Fragment {

    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TabLayout tabLayout;
    private EditText etSearch;

    private OrderRepository orderRepository;
    private List<Order> allOrders = new ArrayList<>();
    private List<Order> filteredByStatusOrders = new ArrayList<>();
    private OrderCardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_orders, container, false);

        orderRepository = new OrderRepository();

        rvOrders = view.findViewById(R.id.rvAdminOrders);
        progressBar = view.findViewById(R.id.progressBarOrders);
        tvEmpty = view.findViewById(R.id.tvEmptyOrders);
        tabLayout = view.findViewById(R.id.tabLayoutAdminOrders);
        etSearch = view.findViewById(R.id.etSearchOrder);

        setupRecyclerView();
        setupTabs();
        setupSearch();
        loadAllOrders();

        return view;
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderCardAdapter(new ArrayList<>(), new OrderCardAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                startActivity(intent);
            }

            @Override
            public void onEditClick(Order order) {}

            @Override
            public void onCancelClick(Order order) {
                // Admin specific cancellation logic can be added here
                Toast.makeText(requireContext(), "Admin order cancellation logic", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMainActionClick(Order order) {}
        });
        rvOrders.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { filterByStatus(tab.getPosition()); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { search(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllOrders() {
        progressBar.setVisibility(View.VISIBLE);
        orderRepository.getAllOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                allOrders = orders;
                filterByStatus(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByStatus(int position) {
        switch (position) {
            case 1: // Pending
                filteredByStatusOrders = allOrders.stream().filter(o -> "Pending".equals(o.getStatus())).collect(Collectors.toList());
                break;
            case 2: // Active
                filteredByStatusOrders = allOrders.stream().filter(o -> "Accepted".equals(o.getStatus()) || "Picked Up".equals(o.getStatus()) || "On the Way".equals(o.getStatus())).collect(Collectors.toList());
                break;
            case 3: // Delivered
                filteredByStatusOrders = allOrders.stream().filter(o -> "Delivered".equals(o.getStatus())).collect(Collectors.toList());
                break;
            case 4: // Cancelled
                filteredByStatusOrders = allOrders.stream().filter(o -> "Cancelled".equals(o.getStatus())).collect(Collectors.toList());
                break;
            default: // All
                filteredByStatusOrders = new ArrayList<>(allOrders);
                break;
        }
        search(etSearch.getText().toString());
    }

    private void search(String query) {
        List<Order> result;
        if (query.isEmpty()) {
            result = new ArrayList<>(filteredByStatusOrders);
        } else {
            result = filteredByStatusOrders.stream()
                    .filter(o -> o.getProductName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        adapter.updateList(result);
        tvEmpty.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
