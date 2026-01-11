package com.example.quickcommercedemo.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.adapters.MultiOrderFormAdapter;
import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.models.OrderFormItem;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderFragment extends Fragment {

    private RecyclerView rvOrderForms;
    private MaterialButton btnAddMore, btnSubmitOrders;
    private Toolbar toolbar;

    private List<OrderFormItem> formItems;
    private MultiOrderFormAdapter adapter;
    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_order, container, false);

        orderRepository = new OrderRepository();
        sessionManager = new SessionManager(requireContext());
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Creating orders...");
        progressDialog.setCancelable(false);

        initViews(view);
        setupRecyclerView();

        return view;
    }

    private void initViews(View view) {
        rvOrderForms = view.findViewById(R.id.rvOrderForms);
        btnAddMore = view.findViewById(R.id.btnAddMore);
        btnSubmitOrders = view.findViewById(R.id.btnSubmitOrders);
        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        btnAddMore.setOnClickListener(v -> {
            formItems.add(new OrderFormItem());
            adapter.notifyItemInserted(formItems.size() - 1);
            rvOrderForms.smoothScrollToPosition(formItems.size() - 1);
        });

        btnSubmitOrders.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupRecyclerView() {
        formItems = new ArrayList<>();
        formItems.add(new OrderFormItem()); // Initial item

        adapter = new MultiOrderFormAdapter(formItems);
        rvOrderForms.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrderForms.setAdapter(adapter);
    }

    private void validateAndSubmit() {
        boolean allValid = true;
        for (OrderFormItem item : formItems) {
            if (!item.isValid()) {
                allValid = false;
                break;
            }
        }

        if (!allValid) {
            Toast.makeText(requireContext(), "Please fill all required fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        submitOrders();
    }

    private void submitOrders() {
        progressDialog.show();
        final int totalOrders = formItems.size();
        final int[] successCount = {0};
        final int[] failureCount = {0};

        String userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        for (OrderFormItem item : formItems) {
            Order order = new Order(
                userId,
                userName,
                item.getCategory(),
                item.getProductName(),
                item.getDescription(),
                item.getLocation(),
                item.getLatitude(),
                item.getLongitude(),
                item.getTimeFrom(),
                item.getTimeTo(),
                item.getDeliveryFee()
            );

            orderRepository.createOrder(order, new OrderRepository.StringCallback() {
                @Override
                public void onSuccess(String orderId) {
                    successCount[0]++;
                    checkCompletion(totalOrders, successCount[0], failureCount[0]);
                }

                @Override
                public void onFailure(Exception e) {
                    failureCount[0]++;
                    checkCompletion(totalOrders, successCount[0], failureCount[0]);
                }
            });
        }
    }

    private void checkCompletion(int total, int success, int failure) {
        if (success + failure == total) {
            progressDialog.dismiss();
            if (failure == 0) {
                Toast.makeText(requireContext(), "All " + total + " orders created successfully!", Toast.LENGTH_LONG).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(requireContext(), success + " orders created, " + failure + " failed.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
