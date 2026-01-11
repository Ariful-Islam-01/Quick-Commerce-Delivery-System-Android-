package com.example.quickcommercedemo.activities;

import android.app.AlertDialog;
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

    private TextView tvProductName, tvStatus, tvDescription, tvLocation, tvTime, tvFee, tvCategory;
    private TextView tvContactRole, tvContactName;
    private LinearLayout layoutDeliveryActions, layoutCustomerActions, cardContact, layoutTimeline;
    private MaterialButton btnWorkflowAction, btnCancelOrder, btnRateDelivery;

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
        tvTime = findViewById(R.id.tvDetailTime);
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

        btnCancelOrder.setOnClickListener(v -> cancelOrder());
        btnRateDelivery.setOnClickListener(v -> showRatingDialog());
    }

    private void loadOrderDetails() {
        orderRepository.getOrderById(orderId, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                currentOrder = order;
                updateUI();
                updateTimeline();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrderDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        tvProductName.setText(currentOrder.getProductName());
        tvStatus.setText(currentOrder.getStatus());
        tvCategory.setText(currentOrder.getCategory());
        tvDescription.setText(currentOrder.getDescription() != null && !currentOrder.getDescription().isEmpty() 
                ? currentOrder.getDescription() : "No description provided");
        tvLocation.setText(currentOrder.getLocation());
        tvTime.setText(currentOrder.getTimeFrom() + " - " + currentOrder.getTimeTo());
        tvFee.setText(String.format("৳%.2f", currentOrder.getDeliveryFee()));

        String userId = sessionManager.getUserId();

        // Role-based visibility
        if (userId.equals(currentOrder.getCustomerId())) {
            layoutCustomerActions.setVisibility(View.VISIBLE);
            layoutDeliveryActions.setVisibility(View.GONE);
            
            btnCancelOrder.setVisibility("Pending".equals(currentOrder.getStatus()) ? View.VISIBLE : View.GONE);
            btnRateDelivery.setVisibility("Delivered".equals(currentOrder.getStatus()) ? View.VISIBLE : View.GONE);

            if (currentOrder.getAcceptedByUserId() != null) {
                cardContact.setVisibility(View.VISIBLE);
                tvContactRole.setText("Delivery Partner");
                tvContactName.setText(currentOrder.getAcceptedByName());
            }
        } else if (userId.equals(currentOrder.getAcceptedByUserId())) {
            layoutDeliveryActions.setVisibility(View.VISIBLE);
            layoutCustomerActions.setVisibility(View.GONE);
            cardContact.setVisibility(View.VISIBLE);
            tvContactRole.setText("Customer");
            tvContactName.setText(currentOrder.getCustomerName());

            updateWorkflowButton();
        } else if ("Pending".equals(currentOrder.getStatus())) {
            // View by potential delivery partner
            layoutDeliveryActions.setVisibility(View.VISIBLE);
            btnWorkflowAction.setText("Accept Delivery");
            btnWorkflowAction.setOnClickListener(v -> acceptOrder());
        }
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
        layoutTimeline.removeAllViews();
        addTimelineItem("Order Created", currentOrder.getCreatedAt());
        if (currentOrder.getUpdatedAt() > currentOrder.getCreatedAt()) {
            addTimelineItem("Status: " + currentOrder.getStatus(), currentOrder.getUpdatedAt());
        }
    }

    private void addTimelineItem(String text, long timestamp) {
        View view = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, layoutTimeline, false);
        TextView tv1 = view.findViewById(android.R.id.text1);
        TextView tv2 = view.findViewById(android.R.id.text2);
        
        tv1.setText(text);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        tv2.setText(sdf.format(new Date(timestamp)));
        
        layoutTimeline.addView(view);
    }

    private void acceptOrder() {
        String userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();
        orderRepository.acceptOrder(orderId, userId, userName, new OrderRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(OrderDetailsActivity.this, "Order accepted!", Toast.LENGTH_SHORT).show();
                loadOrderDetails();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrderDetailsActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String status) {
        orderRepository.updateOrderStatus(orderId, status, new OrderRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(OrderDetailsActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                loadOrderDetails();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OrderDetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deliverOrder() {
        orderRepository.updateOrderStatus(orderId, "Delivered", new OrderRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                Earning earning = new Earning(currentOrder.getAcceptedByUserId(), currentOrder.getOrderId(), 
                        currentOrder.getProductName(), currentOrder.getLocation(), currentOrder.getDeliveryFee());
                earningRepository.createEarning(earning, new EarningRepository.VoidCallback() {
                    @Override public void onSuccess() { loadOrderDetails(); }
                    @Override public void onFailure(Exception e) {}
                });
            }
            @Override
            public void onFailure(Exception e) {}
        });
    }

    private void cancelOrder() {
        orderRepository.updateOrderStatus(orderId, "Cancelled", new OrderRepository.VoidCallback() {
            @Override public void onSuccess() { finish(); }
            @Override public void onFailure(Exception e) {}
        });
    }

    private void showRatingDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText etFeedback = dialogView.findViewById(R.id.etFeedback);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSubmit.setOnClickListener(v -> {
            submitRating(ratingBar.getRating(), etFeedback.getText().toString(), dialog);
        });
        dialog.show();
    }

    private void submitRating(float val, String feedback, AlertDialog dialog) {
        Rating rating = new Rating(currentOrder.getOrderId(), currentOrder.getCustomerId(), 
                currentOrder.getAcceptedByUserId(), val, feedback);
        ratingRepository.submitRating(rating, new RatingRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                btnRateDelivery.setVisibility(View.GONE);
                Toast.makeText(OrderDetailsActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Exception e) {}
        });
    }
}
