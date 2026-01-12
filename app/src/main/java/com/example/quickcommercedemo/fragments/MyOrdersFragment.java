package com.example.quickcommercedemo.fragments;

import android.app.AlertDialog;
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

import com.example.quickcommercedemo.MainActivity;
import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.activities.OrderDetailsActivity;
import com.example.quickcommercedemo.adapters.OrderCardAdapter;
import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyOrdersFragment extends Fragment {

    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TabLayout tabLayout;
    private EditText etSearch;

    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private List<Order> allOrders = new ArrayList<>();
    private List<Order> filteredByStatusOrders = new ArrayList<>();
    private OrderCardAdapter adapter;
    private ValueEventListener orderListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        orderRepository = new OrderRepository();
        sessionManager = new SessionManager(requireContext());

        rvOrders = view.findViewById(R.id.rvOrders);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tabLayout = view.findViewById(R.id.tabLayout);
        etSearch = view.findViewById(R.id.etSearch);

        setupRecyclerView();
        setupTabs();
        setupSearch();
        startListeningForOrders();

        return view;
    }

    private void startListeningForOrders() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        orderListener = orderRepository.listenToOrdersByCustomerId(userId, new OrderRepository.OrdersCallback() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (orderListener != null) {
            orderRepository.removeListener(orderListener);
        }
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderCardAdapter(new ArrayList<>(), new OrderCardAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                viewOrderDetails(order);
            }

            @Override
            public void onEditClick(Order order) {
                // Navigate to CreateOrderFragment in Edit Mode
                CreateOrderFragment editFragment = CreateOrderFragment.newInstance(order.getOrderId());
                ((MainActivity)requireActivity()).navigateToFragment(editFragment);
            }

            @Override
            public void onCancelClick(Order order) {
                confirmAndCancelOrder(order);
            }

            @Override
            public void onDeleteClick(Order order) {
                // Not typically used by customers, but required by interface
            }

            @Override
            public void onMainActionClick(Order order) {
            }
        });
        rvOrders.setAdapter(adapter);
    }

    private void viewOrderDetails(Order order) {
        Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
        intent.putExtra("orderId", order.getOrderId());
        startActivity(intent);
    }

    private void confirmAndCancelOrder(Order order) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    orderRepository.updateOrderStatus(order.getOrderId(), "Cancelled", new OrderRepository.VoidCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(requireContext(), "Order cancelled", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterByStatus(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchOrders(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
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
            case 3: // Completed
                filteredByStatusOrders = allOrders.stream().filter(o -> "Delivered".equals(o.getStatus())).collect(Collectors.toList());
                break;
            case 4: // Cancelled
                filteredByStatusOrders = allOrders.stream().filter(o -> "Cancelled".equals(o.getStatus())).collect(Collectors.toList());
                break;
            default: // All
                filteredByStatusOrders = new ArrayList<>(allOrders);
                break;
        }
        searchOrders(etSearch.getText().toString());
    }

    private void searchOrders(String query) {
        List<Order> searchResult;
        if (query.isEmpty()) {
            searchResult = new ArrayList<>(filteredByStatusOrders);
        } else {
            searchResult = filteredByStatusOrders.stream()
                    .filter(o -> o.getProductName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        adapter.updateList(searchResult);
        tvEmpty.setVisibility(searchResult.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
