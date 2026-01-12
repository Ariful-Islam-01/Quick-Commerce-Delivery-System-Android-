package com.example.quickcommercedemo.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.User;
import com.example.quickcommercedemo.repositories.UserRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditUserDialogFragment extends DialogFragment {

    private User user;
    private UserUpdateListener listener;

    public interface UserUpdateListener {
        void onUserUpdated();
    }

    public static EditUserDialogFragment newInstance(User user, UserUpdateListener listener) {
        EditUserDialogFragment fragment = new EditUserDialogFragment();
        fragment.user = user;
        fragment.listener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_user, null);

        TextInputEditText etName = view.findViewById(R.id.etAdminEditName);
        TextInputEditText etPhone = view.findViewById(R.id.etAdminEditPhone);
        TextInputEditText etAddress = view.findViewById(R.id.etAdminEditAddress);
        MaterialButton btnSave = view.findViewById(R.id.btnAdminSaveUser);
        MaterialButton btnCancel = view.findViewById(R.id.btnAdminCancelUser);

        if (user != null) {
            etName.setText(user.getName());
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getDefaultAddress());
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name is required");
                return;
            }

            user.setName(name);
            user.setPhone(phone);
            user.setDefaultAddress(address);

            new UserRepository().updateUser(user, new UserRepository.VoidCallback() {
                @Override
                public void onSuccess() {
                    if (listener != null) listener.onUserUpdated();
                    dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return dialog;
    }
}
