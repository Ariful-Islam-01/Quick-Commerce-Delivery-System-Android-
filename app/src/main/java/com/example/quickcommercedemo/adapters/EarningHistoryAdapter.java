package com.example.quickcommercedemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.Earning;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EarningHistoryAdapter extends RecyclerView.Adapter<EarningHistoryAdapter.EarningViewHolder> {

    private List<Earning> earnings;

    public EarningHistoryAdapter(List<Earning> earnings) {
        this.earnings = earnings;
    }

    public void updateList(List<Earning> newList) {
        this.earnings = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earning_history, parent, false);
        return new EarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningViewHolder holder, int position) {
        holder.bind(earnings.get(position));
    }

    @Override
    public int getItemCount() {
        return earnings.size();
    }

    static class EarningViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProduct, tvAmount, tvLocation, tvDate;

        public EarningViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(R.id.tvEarningProduct);
            tvAmount = itemView.findViewById(R.id.tvEarningAmount);
            tvLocation = itemView.findViewById(R.id.tvEarningLocation);
            tvDate = itemView.findViewById(R.id.tvEarningDate);
        }

        public void bind(Earning earning) {
            tvProduct.setText(earning.getProductName());
            tvAmount.setText(String.format("+৳%.2f", earning.getAmount()));
            tvLocation.setText(earning.getLocation());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(earning.getEarnedAt())));
        }
    }
}
