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
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageEarningsFragment extends Fragment {

    private RecyclerView rvEarnings;
    private ProgressBar progressBar;
    private TextView tvTotalSystemEarnings, tvEmpty;
    
    private EarningHistoryAdapter adapter;
    private List<Earning> allEarnings = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);

        rvEarnings = view.findViewById(R.id.rvEarnings);
        progressBar = view.findViewById(R.id.progressBar);
        tvTotalSystemEarnings = view.findViewById(R.id.tvTotalEarningsAmount);
        tvEmpty = view.findViewById(R.id.tvEmptyEarnings);

        setupRecyclerView();
        loadAllSystemEarnings();

        return view;
    }

    private void setupRecyclerView() {
        rvEarnings.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EarningHistoryAdapter(new ArrayList<>());
        rvEarnings.setAdapter(adapter);
    }

    private void loadAllSystemEarnings() {
        progressBar.setVisibility(View.VISIBLE);
        
        DatabaseManager.getInstance().getEarningsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                
                allEarnings.clear();
                double total = 0;
                
                for (DataSnapshot child : snapshot.getChildren()) {
                    Earning earning = child.getValue(Earning.class);
                    if (earning != null) {
                        allEarnings.add(earning);
                        total += earning.getAmount();
                    }
                }
                
                allEarnings.sort((e1, e2) -> Long.compare(e2.getEarnedAt(), e1.getEarnedAt()));
                adapter.updateList(allEarnings);
                tvTotalSystemEarnings.setText(String.format("৳%.2f", total));
                tvEmpty.setVisibility(allEarnings.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error fetching earnings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
