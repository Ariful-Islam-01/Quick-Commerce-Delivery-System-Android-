package com.example.quickcommercedemo.fragments;

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
import com.example.quickcommercedemo.adapters.OrderCardAdapter;
import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;

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
        loadDeliveries();

        return view;
    }

    private void setupRecyclerView() {
        rvDeliveries.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderCardAdapter(new ArrayList<>(), new OrderCardAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                // Show delivery details/workflow
                Toast.makeText(requireContext(), "Delivery: " + order.getProductName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionClick(Order order) {
                // Handle status updates here or in a details page
            }
        });
        rvDeliveries.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterDeliveries(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadDeliveries() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        // Note: Using orderRepository since deliveries are currently stored as orders with 'Accepted' status
        orderRepository.getAllOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(List<Order> orders) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                // Filter orders accepted by the current user
                allDeliveries = orders.stream()
                        .filter(o -> userId.equals(o.getAcceptedByUserId()))
                        .collect(Collectors.toList());
                
                filterDeliveries(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
