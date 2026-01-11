package com.example.quickcommercedemo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.quickcommercedemo.MainActivity;
import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.repositories.DeliveryRepository;
import com.example.quickcommercedemo.repositories.EarningRepository;
import com.example.quickcommercedemo.utils.SessionManager;

public class HomeFragment extends Fragment {

    private TextView tvTotalOrders, tvActiveDeliveries, tvTotalEarnings, tvAverageRating;
    private TextView tvUserName, tvWelcome;
    private CardView cardCreateOrder, cardMyOrders, cardDeliveries, cardEarnings;

    private SessionManager sessionManager;
    private OrderRepository orderRepository;
    private DeliveryRepository deliveryRepository;
    private EarningRepository earningRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
        orderRepository = new OrderRepository();
        deliveryRepository = new DeliveryRepository();
        earningRepository = new EarningRepository();

        initViews(view);
        loadUserData();
        loadStatistics();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvActiveDeliveries = view.findViewById(R.id.tvActiveDeliveries);
        tvTotalEarnings = view.findViewById(R.id.tvTotalEarnings);
        tvAverageRating = view.findViewById(R.id.tvAverageRating);

        cardCreateOrder = view.findViewById(R.id.cardCreateOrder);
        cardMyOrders = view.findViewById(R.id.cardMyOrders);
        cardDeliveries = view.findViewById(R.id.cardDeliveries);
        cardEarnings = view.findViewById(R.id.cardEarnings);

        // Set click listeners for navigation
        cardCreateOrder.setOnClickListener(v -> 
            ((MainActivity)requireActivity()).navigateToFragment(new CreateOrderFragment())
        );

        cardMyOrders.setOnClickListener(v -> 
            ((MainActivity)requireActivity()).navigateToFragment(new MyOrdersFragment())
        );

        cardDeliveries.setOnClickListener(v -> 
            ((MainActivity)requireActivity()).navigateToFragment(new MyDeliveriesFragment())
        );

        cardEarnings.setOnClickListener(v -> 
            ((MainActivity)requireActivity()).navigateToFragment(new EarningsFragment())
        );
    }

    private void loadUserData() {
        String userName = sessionManager.getUserName();
        tvUserName.setText(userName != null ? userName : "User");
        tvWelcome.setText("Welcome back!");
    }

    private void loadStatistics() {
        String userId = sessionManager.getUserId();

        // Load total orders
        orderRepository.getOrdersByCustomerId(userId, new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.quickcommercedemo.models.Order> orders) {
                if (isAdded()) {
                    tvTotalOrders.setText(String.valueOf(orders.size()));
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    tvTotalOrders.setText("0");
                }
            }
        });

        // Load active deliveries
        orderRepository.getAllOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.quickcommercedemo.models.Order> orders) {
                if (isAdded()) {
                    long activeCount = orders.stream()
                        .filter(o -> userId.equals(o.getAcceptedByUserId()) && 
                                ("Accepted".equals(o.getStatus()) || "Picked Up".equals(o.getStatus()) || "On the Way".equals(o.getStatus())))
                        .count();
                    tvActiveDeliveries.setText(String.valueOf(activeCount));
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    tvActiveDeliveries.setText("0");
                }
            }
        });

        // Load total earnings
        earningRepository.getEarningsByDeliveryPerson(userId, new EarningRepository.EarningsCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.quickcommercedemo.models.Earning> earnings) {
                if (isAdded()) {
                    double total = earnings.stream()
                        .mapToDouble(com.example.quickcommercedemo.models.Earning::getAmount)
                        .sum();
                    tvTotalEarnings.setText(String.format("৳%.2f", total));
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) {
                    tvTotalEarnings.setText("৳0.00");
                }
            }
        });

        // Set default rating
        tvAverageRating.setText("0.0");
    }
}
