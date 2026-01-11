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
import com.example.quickcommercedemo.models.Earning;
import com.example.quickcommercedemo.repositories.EarningRepository;
import com.example.quickcommercedemo.utils.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EarningsFragment extends Fragment {

    private TextView tvTotalEarningsAmount, tvDailyEarnings, tvWeeklyEarnings, tvEmptyEarnings;
    private RecyclerView rvEarnings;
    private ProgressBar progressBar;

    private EarningRepository earningRepository;
    private SessionManager sessionManager;

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

        rvEarnings.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        loadEarnings();

        return view;
    }

    private void loadEarnings() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = sessionManager.getUserId();

        earningRepository.getEarningsByDeliveryPerson(userId, new EarningRepository.EarningsCallback() {
            @Override
            public void onSuccess(List<Earning> earnings) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                calculateAndDisplayStats(earnings);
                
                if (earnings.isEmpty()) {
                    tvEmptyEarnings.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyEarnings.setVisibility(View.GONE);
                    // Set adapter for earnings history if we have one
                    // For now, let's just show stats as per requirement
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
