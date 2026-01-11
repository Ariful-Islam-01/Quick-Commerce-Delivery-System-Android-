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
import com.example.quickcommercedemo.repositories.EarningRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EarningsFragment extends Fragment {

    private TextView tvTotalEarningsAmount, tvDailyEarnings, tvWeeklyEarnings, tvEmptyEarnings;
    private RecyclerView rvEarnings;
    private ProgressBar progressBar;

    private EarningRepository earningRepository;
    private SessionManager sessionManager;
    private EarningHistoryAdapter adapter;
    private ValueEventListener earningsListener;

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

        setupRecyclerView();
        startListeningForEarnings();

        return view;
    }

    private void setupRecyclerView() {
        rvEarnings.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EarningHistoryAdapter(new ArrayList<>());
        rvEarnings.setAdapter(adapter);
    }

    private void startListeningForEarnings() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        earningsListener = earningRepository.listenToEarningsByDeliveryPerson(userId, new EarningRepository.EarningsCallback() {
            @Override
            public void onSuccess(List<Earning> earnings) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                calculateAndDisplayStats(earnings);
                
                if (earnings.isEmpty()) {
                    tvEmptyEarnings.setVisibility(View.VISIBLE);
                    adapter.updateList(new ArrayList<>());
                } else {
                    tvEmptyEarnings.setVisibility(View.GONE);
                    adapter.updateList(earnings);
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (earningsListener != null) {
            earningRepository.removeListener(earningsListener);
        }
    }

    private void calculateAndDisplayStats(List<Earning> earnings) {
        double total = 0;
        double daily = 0;
        double weekly = 0;

        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);

        for (Earning earning : earnings) {
            total += earning.getAmount();

            Calendar earningDate = Calendar.getInstance();
            earningDate.setTimeInMillis(earning.getEarnedAt());

            if (earningDate.get(Calendar.YEAR) == currentYear) {
                if (earningDate.get(Calendar.DAY_OF_YEAR) == today) {
                    daily += earning.getAmount();
                }
                if (earningDate.get(Calendar.WEEK_OF_YEAR) == currentWeek) {
                    weekly += earning.getAmount();
                }
            }
        }

        tvTotalEarningsAmount.setText(String.format("৳%.2f", total));
        tvDailyEarnings.setText(String.format("৳%.2f", daily));
        tvWeeklyEarnings.setText(String.format("৳%.2f", weekly));
    }
}
