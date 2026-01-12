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
import com.example.quickcommercedemo.adapters.EarningHistoryAdapter;
import com.example.quickcommercedemo.models.Earning;
import com.example.quickcommercedemo.models.Order;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManageEarningsFragment extends Fragment {

    private RecyclerView rvEarnings;
    private TextView tvTotalRevenue, tvTodayPayout, tvPending, tvActive, tvDelivered, tvCancelled;
    
    private EarningHistoryAdapter adapter;
    private List<Earning> allEarnings = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_report, container, false);

        tvTotalRevenue = view.findViewById(R.id.tvReportTotalRevenue);
        tvTodayPayout = view.findViewById(R.id.tvReportTodayPayout);
        tvPending = view.findViewById(R.id.tvCountPending);
        tvActive = view.findViewById(R.id.tvCountActive);
        tvDelivered = view.findViewById(R.id.tvCountDelivered);
        tvCancelled = view.findViewById(R.id.tvCountCancelled);
        rvEarnings = view.findViewById(R.id.rvReportEarnings);

        setupRecyclerView();
        loadSystemReport();

        return view;
    }

    private void setupRecyclerView() {
        rvEarnings.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EarningHistoryAdapter(new ArrayList<>());
        rvEarnings.setAdapter(adapter);
    }

    private void loadSystemReport() {
        // Load Earnings Stats
        DatabaseManager.getInstance().getEarningsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                
                allEarnings.clear();
                double total = 0;
                double today = 0;
                
                Calendar cal = Calendar.getInstance();
                int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
                int year = cal.get(Calendar.YEAR);

                for (DataSnapshot child : snapshot.getChildren()) {
                    Earning earning = child.getValue(Earning.class);
                    if (earning != null) {
                        allEarnings.add(earning);
                        total += earning.getAmount();
                        
                        Calendar earnCal = Calendar.getInstance();
                        earnCal.setTimeInMillis(earning.getEarnedAt());
                        if (earnCal.get(Calendar.YEAR) == year && earnCal.get(Calendar.DAY_OF_YEAR) == dayOfYear) {
                            today += earning.getAmount();
                        }
                    }
                }
                
                allEarnings.sort((e1, e2) -> Long.compare(e2.getEarnedAt(), e1.getEarnedAt()));
                adapter.updateList(allEarnings);
                tvTotalRevenue.setText(String.format("৳%.2f", total));
                tvTodayPayout.setText(String.format("৳%.2f", today));
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Load Order Breakdown
        DatabaseManager.getInstance().getOrdersReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                int p = 0, a = 0, d = 0, c = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Order o = child.getValue(Order.class);
                    if (o != null) {
                        switch (o.getStatus()) {
                            case "Pending": p++; break;
                            case "Accepted": case "Picked Up": case "On the Way": a++; break;
                            case "Delivered": d++; break;
                            case "Cancelled": c++; break;
                        }
                    }
                }
                tvPending.setText("Pending: " + p);
                tvActive.setText("Active: " + a);
                tvDelivered.setText("Delivered: " + d);
                tvCancelled.setText("Cancelled: " + c);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
