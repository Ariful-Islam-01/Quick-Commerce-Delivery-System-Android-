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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyDeliveriesFragment extends Fragment {

    private RecyclerView rvDeliveries;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TabLayout tabLayout;

    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private List<Order> allDeliveries = new ArrayList<>();
    private OrderCardAdapter adapter;
    private ValueEventListener deliveryListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_deliveries, container, false);

        orderRepository = new OrderRepository();
        sessionManager = new SessionManager(requireContext());

        rvDeliveries = view.findViewById(R.id.rvDeliveries);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tabLayout = view.findViewById(R.id.tabLayout);

        setupRecyclerView();
        setupTabs();
        startListeningForDeliveries();

        return view;
    }

    private void setupRecyclerView() {
        rvDeliveries.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Updated to use the 4-argument constructor with isMyTask = true
        adapter = new OrderCardAdapter(new ArrayList<>(), false, true, new OrderCardAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                startActivity(intent);
            }
            @Override public void onEditClick(Order order) {}
            @Override public void onCancelClick(Order order) {}
            @Override public void onDeleteClick(Order order) {} // Fixed: Implemented missing method
            
            @Override 
            public void onMainActionClick(Order order) {
                // When "Update Status" is clicked, we just open details 
                // where the workflow buttons are already implemented.
                Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                startActivity(intent);
            }
        });
        rvDeliveries.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { filterDeliveries(tab.getPosition()); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void startListeningForDeliveries() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        deliveryListener = orderRepository.listenToAcceptedDeliveries(userId, new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                allDeliveries = orders;
                filterDeliveries(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void filterDeliveries(int position) {
        List<Order> filteredList;
        switch (position) {
            case 0: // Ongoing
                filteredList = allDeliveries.stream()
                        .filter(o -> "Accepted".equals(o.getStatus()) || "Picked Up".equals(o.getStatus()) || "On the Way".equals(o.getStatus()))
                        .collect(Collectors.toList());
                break;
            case 1: // Completed
                filteredList = allDeliveries.stream()
                        .filter(o -> "Delivered".equals(o.getStatus()))
                        .collect(Collectors.toList());
                break;
            default:
                filteredList = new ArrayList<>(allDeliveries);
                break;
        }

        adapter.updateList(filteredList);
        tvEmpty.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (deliveryListener != null) orderRepository.removeListener(deliveryListener);
    }
}
