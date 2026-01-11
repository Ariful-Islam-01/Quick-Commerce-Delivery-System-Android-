package com.example.quickcommercedemo.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.User;
import com.google.android.material.button.MaterialButton;

public class UserDetailsDialogFragment extends DialogFragment {

    private User user;

    public static UserDetailsDialogFragment newInstance(User user) {
        UserDetailsDialogFragment fragment = new UserDetailsDialogFragment();
        fragment.user = user;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(android.R.layout.simple_list_item_2, null);
        TextView tvLabel = view.findViewById(android.R.id.text1);
        TextView tvInfo = view.findViewById(android.R.id.text2);

        if (user != null) {
            tvLabel.setText(user.getName());
            tvInfo.setText("Email: " + user.getEmail() + "\nPhone: " + user.getPhone() + "\nAddress: " + user.getDefaultAddress());
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("User Information")
                .setView(view)
                .setPositiveButton("Close", null)
                .create();
    }
}
