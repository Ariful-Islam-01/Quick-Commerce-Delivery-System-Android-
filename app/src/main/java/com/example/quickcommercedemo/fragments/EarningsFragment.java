package com.example.quickcommercedemo.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
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

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.adapters.EarningHistoryAdapter;
import com.example.quickcommercedemo.models.Earning;
import com.example.quickcommercedemo.repositories.EarningRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EarningsFragment extends Fragment {

    private TextView tvTotalEarningsAmount, tvDailyEarnings, tvWeeklyEarnings, tvEmptyEarnings;
    private RecyclerView rvEarnings;
    private ProgressBar progressBar;
    private EditText etDateFilter;

    private EarningRepository earningRepository;
    private SessionManager sessionManager;
    private EarningHistoryAdapter adapter;
    private ValueEventListener earningsListener;
    
    private List<OrderEarning> allEarningsData = new ArrayList<>();
    private String selectedDateFilter = "";

    // Internal class to hold processed earning data
    private static class OrderEarning extends Earning {
        public OrderEarning(Earning e) {
            setEarningId(e.getEarningId());
            setDeliveryPersonId(e.getDeliveryPersonId());
            setOrderId(e.getOrderId());
            setProductName(e.getProductName());
            setLocation(e.getLocation());
            setAmount(e.getAmount());
            setEarnedAt(e.getEarnedAt());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        earningRepository = new EarningRepository();
        sessionManager = new SessionManager(requireContext());

        tvTotalEarningsAmount = view.findViewById(R.id.tvTotalEarningsAmount);
        tvDailyEarnings = view.findViewById(R.id.tvDailyEarnings);
        tvWeeklyEarnings = view.findViewById(R.id.tvWeeklyEarnings);
        tvEmptyEarnings = view.findViewById(R.id.tvEmptyEarnings);
        rvEarnings = view.findViewById(R.id.rvEarnings);
        progressBar = view.findViewById(R.id.progressBar);
        etDateFilter = view.findViewById(R.id.etDateFilter);

        setupRecyclerView();
        setupDateFilter();
        startListeningForEarnings();

        return view;
    }

    private void setupRecyclerView() {
        rvEarnings.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EarningHistoryAdapter(new ArrayList<>());
        rvEarnings.setAdapter(adapter);
    }

    private void setupDateFilter() {
        etDateFilter.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDateFilter = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etDateFilter.setText(selectedDateFilter);
                        applyFilter();
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear", (dialog, which) -> {
                selectedDateFilter = "";
                etDateFilter.setText("");
                applyFilter();
            });
            
            datePickerDialog.show();
        });
    }

    private void startListeningForEarnings() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        earningsListener = earningRepository.listenToEarningsByDeliveryPerson(userId, new EarningRepository.EarningsCallback() {
            @Override
            public void onSuccess(List<Earning> earnings) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                allEarningsData = earnings.stream().map(OrderEarning::new).collect(Collectors.toList());
                calculateAndDisplayStats(earnings);
                applyFilter();
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded()) progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void applyFilter() {
        List<OrderEarning> filtered;
        if (selectedDateFilter.isEmpty()) {
            filtered = new ArrayList<>(allEarningsData);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            filtered = allEarningsData.stream().filter(e -> {
                String earningDate = sdf.format(new Date(e.getEarnedAt()));
                return earningDate.equals(selectedDateFilter);
            }).collect(Collectors.toList());
        }

        adapter.updateList(new ArrayList<>(filtered));
        tvEmptyEarnings.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void calculateAndDisplayStats(List<Earning> earnings) {
        double total = 0, daily = 0, weekly = 0;
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);

        for (Earning earning : earnings) {
            total += earning.getAmount();
            Calendar earningDate = Calendar.getInstance();
            earningDate.setTimeInMillis(earning.getEarnedAt());

            if (earningDate.get(Calendar.YEAR) == currentYear) {
                if (earningDate.get(Calendar.DAY_OF_YEAR) == today) daily += earning.getAmount();
                if (earningDate.get(Calendar.WEEK_OF_YEAR) == currentWeek) weekly += earning.getAmount();
            }
        }

        tvTotalEarningsAmount.setText(String.format("৳%.2f", total));
        tvDailyEarnings.setText(String.format("৳%.2f", daily));
        tvWeeklyEarnings.setText(String.format("৳%.2f", weekly));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (earningsListener != null) earningRepository.removeListener(earningsListener);
    }
}
