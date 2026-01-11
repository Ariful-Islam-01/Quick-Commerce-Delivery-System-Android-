package com.example.quickcommercedemo.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.Rating;
import com.example.quickcommercedemo.repositories.RatingRepository;
import com.google.android.material.button.MaterialButton;

public class RatingDialogFragment extends DialogFragment {

    private String orderId, customerId, deliveryPersonId;
    private RatingCallback callback;

    public interface RatingCallback {
        void onRatingSubmitted();
    }

    public static RatingDialogFragment newInstance(String orderId, String customerId, String deliveryPersonId, RatingCallback callback) {
        RatingDialogFragment fragment = new RatingDialogFragment();
        fragment.orderId = orderId;
        fragment.customerId = customerId;
        fragment.deliveryPersonId = deliveryPersonId;
        fragment.callback = callback;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rating, null);
        
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText etFeedback = view.findViewById(R.id.etFeedback);
        MaterialButton btnSubmit = view.findViewById(R.id.btnSubmit);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        btnCancel.setOnClickListener(v -> dismiss());
        btnSubmit.setOnClickListener(v -> {
            float value = ratingBar.getRating();
            if (value == 0) {
                Toast.makeText(getContext(), "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            submitRating(value, etFeedback.getText().toString());
        });

        return dialog;
    }

    private void submitRating(float val, String feedback) {
        Rating rating = new Rating(orderId, customerId, deliveryPersonId, val, feedback);
        new RatingRepository().submitRating(rating, new RatingRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onRatingSubmitted();
                dismiss();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to rate", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
