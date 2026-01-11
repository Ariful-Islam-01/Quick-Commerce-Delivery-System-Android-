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

    private static final String ARG_EDIT_ORDER_ID = "edit_order_id";

    private RecyclerView rvOrderForms;
    private MaterialButton btnAddMore, btnSubmitOrders;
    private Toolbar toolbar;

    private List<OrderFormItem> formItems;
    private MultiOrderFormAdapter adapter;
    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    
    private String editOrderId = null;

    public static CreateOrderFragment newInstance(String orderId) {
        CreateOrderFragment fragment = new CreateOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EDIT_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editOrderId = getArguments().getString(ARG_EDIT_ORDER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_order, container, false);

        orderRepository = new OrderRepository();
        sessionManager = new SessionManager(requireContext());
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage(editOrderId != null ? "Updating order..." : "Creating orders...");
        progressDialog.setCancelable(false);

        initViews(view);
        setupRecyclerView();

        if (editOrderId != null) {
            loadOrderToEdit();
            btnAddMore.setVisibility(View.GONE); // Hide "Add More" when editing a single specific order
            toolbar.setTitle("Edit Order");
        }

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
        if (editOrderId == null) {
            formItems.add(new OrderFormItem());
        }

        adapter = new MultiOrderFormAdapter(formItems);
        rvOrderForms.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrderForms.setAdapter(adapter);
    }

    private void loadOrderToEdit() {
        orderRepository.getOrderById(editOrderId, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                if (!isAdded()) return;
                OrderFormItem item = new OrderFormItem();
                item.setCategory(order.getCategory());
                item.setProductName(order.getProductName());
                item.setDescription(order.getDescription());
                item.setLocation(order.getLocation());
                item.setDeliveryDate(order.getDeliveryDate());
                item.setTimeFrom(order.getTimeFrom());
                item.setTimeTo(order.getTimeTo());
                item.setDeliveryFee(order.getDeliveryFee());
                
                formItems.clear();
                formItems.add(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) Toast.makeText(requireContext(), "Error loading order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndSubmit() {
        for (OrderFormItem item : formItems) {
            if (!item.isValid()) {
                Toast.makeText(requireContext(), "Please fill all required fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        submitOrders();
    }

    private void submitOrders() {
        progressDialog.show();
        String userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        if (editOrderId != null) {
            // Update single order
            OrderFormItem item = formItems.get(0);
            orderRepository.getOrderById(editOrderId, new OrderRepository.OrderCallback() {
                @Override
                public void onSuccess(Order order) {
                    order.setCategory(item.getCategory());
                    order.setProductName(item.getProductName());
                    order.setDescription(item.getDescription());
                    order.setLocation(item.getLocation());
                    order.setDeliveryDate(item.getDeliveryDate());
                    order.setTimeFrom(item.getTimeFrom());
                    order.setTimeTo(item.getTimeTo());
                    order.setDeliveryFee(item.getDeliveryFee());
                    order.setUpdatedAt(System.currentTimeMillis());

                    orderRepository.createOrder(order, new OrderRepository.StringCallback() { // createOrder handles setValue(order) which works for updates
                        @Override
                        public void onSuccess(String id) {
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), "Order updated!", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                        @Override
                        public void onFailure(Exception e) { progressDialog.dismiss(); }
                    });
                }
                @Override
                public void onFailure(Exception e) { progressDialog.dismiss(); }
            });
        } else {
            // Create multiple orders
            final int total = formItems.size();
            final int[] done = {0};
            for (OrderFormItem item : formItems) {
                Order order = new Order(userId, userName, item.getCategory(), item.getProductName(),
                        item.getDescription(), item.getLocation(), 0, 0,
                        item.getDeliveryDate(), item.getTimeFrom(), item.getTimeTo(), item.getDeliveryFee());
                
                orderRepository.createOrder(order, new OrderRepository.StringCallback() {
                    @Override
                    public void onSuccess(String id) { check(++done[0], total); }
                    @Override
                    public void onFailure(Exception e) { check(++done[0], total); }
                });
            }
        }
    }

    private void check(int done, int total) {
        if (done == total) {
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "Orders created!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
