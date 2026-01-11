package com.example.quickcommercedemo.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
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
        AutoCompleteTextView spinnerCategory;
        EditText etProductName, etDescription, etLocation, etDeliveryDate, etTimeFrom, etTimeTo, etFee, etNotes;
        
        private CustomTextWatcher nameWatcher, descWatcher, locWatcher, feeWatcher, notesWatcher;

        public FormViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            spinnerCategory = itemView.findViewById(R.id.spinnerCategory);
            etProductName = itemView.findViewById(R.id.etProductName);
            etDescription = itemView.findViewById(R.id.etDescription);
            etLocation = itemView.findViewById(R.id.etLocation);
            etDeliveryDate = itemView.findViewById(R.id.etDeliveryDate);
            etTimeFrom = itemView.findViewById(R.id.etTimeFrom);
            etTimeTo = itemView.findViewById(R.id.etTimeTo);
            etFee = itemView.findViewById(R.id.etFee);
            etNotes = itemView.findViewById(R.id.etNotes);

            setupCategorySpinner();
            setupDateTimePickers();
            setupTextWatchers();
        }

        private void setupCategorySpinner() {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line, categories);
            spinnerCategory.setAdapter(adapter);

            spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    items.get(pos).setCategory(categories[position]);
                }
            });
        }

        private void setupDateTimePickers() {
            etDeliveryDate.setOnClickListener(v -> showDatePicker());
            etTimeFrom.setOnClickListener(v -> showTimePicker(etTimeFrom, true));
            etTimeTo.setOnClickListener(v -> showTimePicker(etTimeTo, false));
        }

        private void showDatePicker() {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(itemView.getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etDeliveryDate.setText(date);
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            items.get(pos).setDeliveryDate(date);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
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

        private void setupTextWatchers() {
            nameWatcher = new CustomTextWatcher(s -> items.get(getAdapterPosition()).setProductName(s));
            descWatcher = new CustomTextWatcher(s -> items.get(getAdapterPosition()).setDescription(s));
            locWatcher = new CustomTextWatcher(s -> items.get(getAdapterPosition()).setLocation(s));
            notesWatcher = new CustomTextWatcher(s -> items.get(getAdapterPosition()).setNotes(s));
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
            etNotes.addTextChangedListener(notesWatcher);
            etFee.addTextChangedListener(feeWatcher);
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

            nameWatcher.setActive(false);
            descWatcher.setActive(false);
            locWatcher.setActive(false);
            notesWatcher.setActive(false);
            feeWatcher.setActive(false);

            etProductName.setText(item.getProductName());
            etDescription.setText(item.getDescription());
            etLocation.setText(item.getLocation());
            etDeliveryDate.setText(item.getDeliveryDate());
            etTimeFrom.setText(item.getTimeFrom());
            etTimeTo.setText(item.getTimeTo());
            etNotes.setText(item.getNotes());
            etFee.setText(item.getDeliveryFee() > 0 ? String.valueOf(item.getDeliveryFee()) : "");
            spinnerCategory.setText(item.getCategory(), false);

            nameWatcher.setActive(true);
            descWatcher.setActive(true);
            locWatcher.setActive(true);
            notesWatcher.setActive(true);
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
