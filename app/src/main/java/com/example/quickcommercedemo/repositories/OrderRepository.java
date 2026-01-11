package com.example.quickcommercedemo.repositories;

import androidx.annotation.NonNull;

import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private final DatabaseReference ordersRef;

    public OrderRepository() {
        this.ordersRef = DatabaseManager.getInstance().getOrdersReference();
    }

    public interface OrderCallback {
        void onSuccess(Order order);
        void onFailure(Exception e);
    }

    public interface OrdersCallback {
        void onSuccess(List<Order> orders);
        void onFailure(Exception e);
    }

    public interface VoidCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface StringCallback {
        void onSuccess(String orderId);
        void onFailure(Exception e);
    }

    public ValueEventListener listenToOrdersByCustomerId(String customerId, OrdersCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Order order = childSnapshot.getValue(Order.class);
                    if (order != null) orders.add(order);
                }
                orders.sort((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));
                callback.onSuccess(orders);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.toException()); }
        };
        ordersRef.orderByChild("customerId").equalTo(customerId).addValueEventListener(listener);
        return listener;
    }

    public ValueEventListener listenToPendingOrders(OrdersCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Order order = childSnapshot.getValue(Order.class);
                    if (order != null) orders.add(order);
                }
                orders.sort((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));
                callback.onSuccess(orders);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.toException()); }
        };
        ordersRef.orderByChild("status").equalTo("Pending").addValueEventListener(listener);
        return listener;
    }

    public ValueEventListener listenToAcceptedDeliveries(String userId, OrdersCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Order order = childSnapshot.getValue(Order.class);
                    if (order != null && userId.equals(order.getAcceptedByUserId())) {
                        orders.add(order);
                    }
                }
                orders.sort((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));
                callback.onSuccess(orders);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.toException()); }
        };
        ordersRef.addValueEventListener(listener);
        return listener;
    }

    public void removeListener(ValueEventListener listener) {
        if (listener != null) ordersRef.removeEventListener(listener);
    }

    public void createOrder(Order order, StringCallback callback) {
        String orderId = order.getOrderId();
        if (orderId == null) orderId = ordersRef.push().getKey();
        if (orderId != null) {
            order.setOrderId(orderId);
            ordersRef.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> callback.onSuccess(order.getOrderId()))
                .addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("Failed to generate order ID"));
        }
    }

    public void getOrderById(String orderId, OrderCallback callback) {
        ordersRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) callback.onSuccess(order);
                    else callback.onFailure(new Exception("Failed to parse order"));
                } else callback.onFailure(new Exception("Order not found"));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.toException()); }
        });
    }

    public void getOrdersByCustomerId(String customerId, OrdersCallback callback) {
        ordersRef.orderByChild("customerId").equalTo(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Order order = childSnapshot.getValue(Order.class);
                    if (order != null) orders.add(order);
                }
                orders.sort((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));
                callback.onSuccess(orders);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.toException()); }
        });
    }

    public void getPendingOrders(OrdersCallback callback) {
        ordersRef.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Order order = childSnapshot.getValue(Order.class);
                    if (order != null) orders.add(order);
                }
                orders.sort((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));
                callback.onSuccess(orders);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.toException()); }
        });
    }

    public void acceptOrder(String orderId, String deliveryPersonId, String deliveryPersonName, VoidCallback callback) {
        ordersRef.child(orderId).child("status").setValue("Accepted");
        ordersRef.child(orderId).child("acceptedByUserId").setValue(deliveryPersonId);
        ordersRef.child(orderId).child("acceptedByName").setValue(deliveryPersonName);
        ordersRef.child(orderId).child("updatedAt").setValue(System.currentTimeMillis())
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }

    public void updateOrderStatus(String orderId, String status, VoidCallback callback) {
        ordersRef.child(orderId).child("status").setValue(status);
        ordersRef.child(orderId).child("updatedAt").setValue(System.currentTimeMillis())
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }

    public void getAllOrders(OrdersCallback callback) {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Order order = childSnapshot.getValue(Order.class);
                    if (order != null) orders.add(order);
                }
                orders.sort((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));
                callback.onSuccess(orders);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onFailure(error.toException()); }
        });
    }

    public void deleteOrder(String orderId, VoidCallback callback) {
        ordersRef.child(orderId).removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }
}
