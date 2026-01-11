package com.example.quickcommercedemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;

import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatViewHolder> {

    private List<StatItem> stats;

    public static class StatItem {
        String label;
        String value;
        public StatItem(String label, String value) { this.label = label; this.value = value; }
    }

    public StatisticsAdapter(List<StatItem> stats) {
        this.stats = stats;
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using fragment_home's card-like structure logic or a dedicated layout
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        StatItem item = stats.get(position);
        holder.tvLabel.setText(item.label);
        holder.tvValue.setText(item.value);
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    static class StatViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;
        public StatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(android.R.id.text1);
            tvValue = itemView.findViewById(android.R.id.text2);
        }
    }
}
