package com.example.quickcommercedemo.adapters;

import android.app.TimePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.OrderFormItem;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MultiOrderFormAdapter extends RecyclerView.Adapter<MultiOrderFormAdapter.FormViewHolder> {

    private final List<OrderFormItem> items;
    private final String[] categories = {"Groceries", "Food", "Electronics", "Pharma", "Fashion", "Others"};

    public MultiOrderFormAdapter(List<OrderFormItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_form, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class FormViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber;
        ImageButton btnRemove;
        Spinner spinnerCategory;
        EditText etProductName, etDescription, etLocation, etTimeFrom, etTimeTo, etFee;
        
        private CustomTextWatcher nameWatcher, descWatcher, locWatcher, feeWatcher;

        public FormViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            spinnerCategory = itemView.findViewById(R.id.spinnerCategory);
            etProductName = itemView.findViewById(R.id.etProductName);
            etDescription = itemView.findViewById(R.id.etDescription);
            etLocation = itemView.findViewById(R.id.etLocation);
            etTimeFrom = itemView.findViewById(R.id.etTimeFrom);
            etTimeTo = itemView.findViewById(R.id.etTimeTo);
            etFee = itemView.findViewById(R.id.etFee);

            setupCategorySpinner();
            setupTimePickers();
            
            // Initialize watchers
            nameWatcher = new CustomTextWatcher(s -> items.get(getAdapterPosition()).setProductName(s));
            descWatcher = new CustomTextWatcher(s -> items.get(getAdapterPosition()).setDescription(s));
            locWatcher = new CustomTextWatcher(s -> items.get(getAdapterPosition()).setLocation(s));
            feeWatcher = new CustomTextWatcher(s -> {
                try {
                    items.get(getAdapterPosition()).setDeliveryFee(Double.parseDouble(s));
                } catch (Exception e) {
                    items.get(getAdapterPosition()).setDeliveryFee(0.0);
                }
            });

            etProductName.addTextChangedListener(nameWatcher);
            etDescription.addTextChangedListener(descWatcher);
            etLocation.addTextChangedListener(locWatcher);
            etFee.addTextChangedListener(feeWatcher);
        }

        private void setupCategorySpinner() {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        items.get(pos).setCategory(categories[position]);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        private void setupTimePickers() {
            etTimeFrom.setOnClickListener(v -> showTimePicker(etTimeFrom, true));
            etTimeTo.setOnClickListener(v -> showTimePicker(etTimeTo, false));
        }

        private void showTimePicker(EditText editText, boolean isFrom) {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(itemView.getContext(),
                    (view, hourOfDay, minuteOfHour) -> {
                        String amPm = hourOfDay >= 12 ? "PM" : "AM";
                        int displayHour = hourOfDay > 12 ? hourOfDay - 12 : (hourOfDay == 0 ? 12 : hourOfDay);
                        String time = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minuteOfHour, amPm);
                        editText.setText(time);
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            if (isFrom) items.get(pos).setTimeFrom(time);
                            else items.get(pos).setTimeTo(time);
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        }

        public void bind(OrderFormItem item, int position) {
            tvOrderNumber.setText("Order #" + (position + 1));
            btnRemove.setVisibility(items.size() > 1 ? View.VISIBLE : View.GONE);
            btnRemove.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    items.remove(pos);
                    notifyDataSetChanged();
                }
            });

            // Temporarily disable watchers to prevent recycling issues
            nameWatcher.setActive(false);
            descWatcher.setActive(false);
            locWatcher.setActive(false);
            feeWatcher.setActive(false);

            etProductName.setText(item.getProductName());
            etDescription.setText(item.getDescription());
            etLocation.setText(item.getLocation());
            etTimeFrom.setText(item.getTimeFrom());
            etTimeTo.setText(item.getTimeTo());
            etFee.setText(item.getDeliveryFee() > 0 ? String.valueOf(item.getDeliveryFee()) : "");
            
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(item.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }

            nameWatcher.setActive(true);
            descWatcher.setActive(true);
            locWatcher.setActive(true);
            feeWatcher.setActive(true);
        }
    }

    private static class CustomTextWatcher implements TextWatcher {
        private final OnTextChange listener;
        private boolean active = true;
        public CustomTextWatcher(OnTextChange listener) { this.listener = listener; }
        public void setActive(boolean active) { this.active = active; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (active) listener.onChange(s.toString());
        }
        @Override public void afterTextChanged(Editable s) {}
        interface OnTextChange { void onChange(String s); }
    }
}
