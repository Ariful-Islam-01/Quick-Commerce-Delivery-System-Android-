package com.example.quickcommercedemo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.Earning;
import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.models.Rating;
import com.example.quickcommercedemo.repositories.EarningRepository;
import com.example.quickcommercedemo.repositories.OrderRepository;
import com.example.quickcommercedemo.repositories.RatingRepository;
import com.example.quickcommercedemo.repositories.UserRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView tvProductName, tvStatus, tvDescription, tvLocation, tvTimeValue, tvFee, tvCategory, tvDateValue;
    private TextView tvContactRole, tvContactName;
    private LinearLayout layoutDeliveryActions, layoutCustomerActions, layoutTimeline;
    private View cardContact; // Changed from LinearLayout to View to fix ClassCastException
    private MaterialButton btnWorkflowAction, btnCancelOrder, btnRateDelivery, btnCall, btnMessage;

    private OrderRepository orderRepository;
    private EarningRepository earningRepository;
    private RatingRepository ratingRepository;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private Order currentOrder;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderId = getIntent().getStringExtra("orderId");
        if (orderId == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderRepository = new OrderRepository();
        earningRepository = new EarningRepository();
        ratingRepository = new RatingRepository();
        userRepository = new UserRepository();
        sessionManager = new SessionManager(this);

        initViews();
        loadOrderDetails();
    }

    private void initViews() {
        tvProductName = findViewById(R.id.tvDetailProductName);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvDateValue = findViewById(R.id.tvDetailDateValue);
        tvTimeValue = findViewById(R.id.tvDetailTimeValue);
        tvFee = findViewById(R.id.tvDetailFee);

        tvContactRole = findViewById(R.id.tvContactRole);
        tvContactName = findViewById(R.id.tvContactName);
        cardContact = findViewById(R.id.cardContact);
        layoutTimeline = findViewById(R.id.layoutTimeline);

        layoutDeliveryActions = findViewById(R.id.layoutDeliveryActions);
        layoutCustomerActions = findViewById(R.id.layoutCustomerActions);
        
        btnWorkflowAction = findViewById(R.id.btnWorkflowAction);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnRateDelivery = findViewById(R.id.btnRateDelivery);
        btnCall = findViewById(R.id.btnCall);
        btnMessage = findViewById(R.id.btnMessage);

        if (btnCancelOrder != null) btnCancelOrder.setOnClickListener(v -> cancelOrder());
        if (btnRateDelivery != null) btnRateDelivery.setOnClickListener(v -> showRatingDialog());
    }

    private void loadOrderDetails() {
        orderRepository.getOrderById(orderId, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                if (isFinishing()) return;
                currentOrder = order;
                updateUI();
                updateTimeline();
            }

            @Override
            public void onFailure(Exception e) {
                if (isFinishing()) return;
                Toast.makeText(OrderDetailsActivity.this, "Failed to load: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentOrder == null || isFinishing()) return;

        tvProductName.setText(safeString(currentOrder.getProductName(), "No Name"));
        tvStatus.setText(safeString(currentOrder.getStatus(), "Pending"));
        tvCategory.setText(safeString(currentOrder.getCategory(), "General"));
        tvDescription.setText(safeString(currentOrder.getDescription(), "No description provided"));
        tvLocation.setText(safeString(currentOrder.getLocation(), "No location provided"));
        tvDateValue.setText(safeString(currentOrder.getDeliveryDate(), "Not set"));
        tvTimeValue.setText(String.format("%s - %s", safeString(currentOrder.getTimeFrom(), "Anytime"), safeString(currentOrder.getTimeTo(), "Anytime")));
        tvFee.setText(String.format(Locale.getDefault(), "৳%.2f", currentOrder.getDeliveryFee()));

        String userId = sessionManager.getUserId();
        if (userId == null) return;

        // Role-based logic
        if (userId.equals(currentOrder.getCustomerId())) {
            showCustomerView();
        } else if (userId.equals(currentOrder.getAcceptedByUserId())) {
            showDeliveryPartnerView();
        } else if ("Pending".equals(currentOrder.getStatus())) {
            showPotentialPartnerView();
        }
    }

    private String safeString(String input, String fallback) {
        return (input == null || input.isEmpty()) ? fallback : input;
    }

    private void showCustomerView() {
        layoutCustomerActions.setVisibility(View.VISIBLE);
        layoutDeliveryActions.setVisibility(View.GONE);
        btnCancelOrder.setVisibility("Pending".equals(currentOrder.getStatus()) ? View.VISIBLE : View.GONE);
        btnRateDelivery.setVisibility("Delivered".equals(currentOrder.getStatus()) ? View.VISIBLE : View.GONE);

        if (currentOrder.getAcceptedByUserId() != null) {
            cardContact.setVisibility(View.VISIBLE);
            tvContactRole.setText("Delivery Partner");
            tvContactName.setText(safeString(currentOrder.getAcceptedByName(), "Partner"));
            setupContactButtons(currentOrder.getAcceptedByPhone());
        }
    }

    private void showDeliveryPartnerView() {
        layoutDeliveryActions.setVisibility(View.VISIBLE);
        layoutCustomerActions.setVisibility(View.GONE);
        cardContact.setVisibility(View.VISIBLE);
        tvContactRole.setText("Customer");
        tvContactName.setText(safeString(currentOrder.getCustomerName(), "Customer"));
        setupContactButtons(currentOrder.getCustomerPhone());
        updateWorkflowButton();
    }

    private void showPotentialPartnerView() {
        layoutDeliveryActions.setVisibility(View.VISIBLE);
        btnWorkflowAction.setText("Accept Delivery");
        btnWorkflowAction.setOnClickListener(v -> acceptOrder());
        cardContact.setVisibility(View.GONE);
    }

    private void setupContactButtons(String phone) {
        if (phone == null || phone.isEmpty()) {
            btnCall.setEnabled(false);
            btnMessage.setEnabled(false);
            return;
        }
        btnCall.setEnabled(true);
        btnMessage.setEnabled(true);
        btnCall.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))));
        btnMessage.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone))));
    }

    private void updateWorkflowButton() {
        switch (currentOrder.getStatus()) {
            case "Accepted":
                btnWorkflowAction.setText("Mark as Picked Up");
                btnWorkflowAction.setOnClickListener(v -> updateStatus("Picked Up"));
                break;
            case "Picked Up":
                btnWorkflowAction.setText("Mark as On the Way");
                btnWorkflowAction.setOnClickListener(v -> updateStatus("On the Way"));
                break;
            case "On the Way":
                btnWorkflowAction.setText("Mark as Delivered");
                btnWorkflowAction.setOnClickListener(v -> deliverOrder());
                break;
            default:
                layoutDeliveryActions.setVisibility(View.GONE);
                break;
        }
    }

    private void updateTimeline() {
        if (currentOrder == null || layoutTimeline == null) return;
        layoutTimeline.removeAllViews();
        addTimelineItem("Order Created", currentOrder.getCreatedAt());
        if (currentOrder.getUpdatedAt() > currentOrder.getCreatedAt()) {
            addTimelineItem("Current Status: " + currentOrder.getStatus(), currentOrder.getUpdatedAt());
        }
    }

    private void addTimelineItem(String text, long timestamp) {
        if (timestamp <= 0) return;
        View view = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, layoutTimeline, false);
        TextView tv1 = view.findViewById(android.R.id.text1);
        TextView tv2 = view.findViewById(android.R.id.text2);
        tv1.setText(text);
        tv2.setText(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date(timestamp)));
        layoutTimeline.addView(view);
    }

    private void acceptOrder() {
        orderRepository.acceptOrder(orderId, sessionManager.getUserId(), sessionManager.getUserName(), new OrderRepository.VoidCallback() {
            @Override
            public void onSuccess() { loadOrderDetails(); }
            @Override
            public void onFailure(Exception e) { Toast.makeText(OrderDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show(); }
        });
    }

    private void updateStatus(String status) {
        orderRepository.updateOrderStatus(orderId, status, new OrderRepository.VoidCallback() {
            @Override public void onSuccess() { loadOrderDetails(); }
            @Override public void onFailure(Exception e) { Toast.makeText(OrderDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void deliverOrder() {
        orderRepository.updateOrderStatus(orderId, "Delivered", new OrderRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                if (isFinishing()) return;
                Earning earning = new Earning(currentOrder.getAcceptedByUserId(), currentOrder.getOrderId(), 
                        currentOrder.getProductName(), currentOrder.getLocation(), currentOrder.getDeliveryFee());
                earningRepository.createEarning(earning, new EarningRepository.VoidCallback() {
                    @Override public void onSuccess() { loadOrderDetails(); }
                    @Override public void onFailure(Exception e) { loadOrderDetails(); }
                });
            }
            @Override public void onFailure(Exception e) {}
        });
    }

    private void cancelOrder() {
        orderRepository.updateOrderStatus(orderId, "Cancelled", new OrderRepository.VoidCallback() {
            @Override public void onSuccess() { finish(); }
            @Override public void onFailure(Exception e) { Toast.makeText(OrderDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showRatingDialog() {
        View dv = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null);
        RatingBar rb = dv.findViewById(R.id.ratingBar);
        EditText ef = dv.findViewById(R.id.etFeedback);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dv).create();
        dv.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dv.findViewById(R.id.btnSubmit).setOnClickListener(v -> submitRating(rb.getRating(), ef.getText().toString(), dialog));
        dialog.show();
    }

    private void submitRating(float val, String feedback, AlertDialog dialog) {
        Rating rating = new Rating(currentOrder.getOrderId(), currentOrder.getCustomerId(), 
                currentOrder.getAcceptedByUserId(), val, feedback);
        ratingRepository.submitRating(rating, new RatingRepository.VoidCallback() {
            @Override public void onSuccess() {
                dialog.dismiss();
                btnRateDelivery.setVisibility(View.GONE);
                Toast.makeText(OrderDetailsActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Exception e) {}
        });
    }
}
