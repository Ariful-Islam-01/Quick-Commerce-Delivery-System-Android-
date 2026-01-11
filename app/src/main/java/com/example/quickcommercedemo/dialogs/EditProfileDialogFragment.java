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

public class EditProfileDialogFragment extends DialogFragment {

    private User user;
    private ProfileUpdateListener listener;

    public interface ProfileUpdateListener {
        void onProfileUpdated();
    }

    public static EditProfileDialogFragment newInstance(User user, ProfileUpdateListener listener) {
        EditProfileDialogFragment fragment = new EditProfileDialogFragment();
        fragment.user = user;
        fragment.listener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);

        TextInputEditText etName = view.findViewById(R.id.etDialogName);
        TextInputEditText etPhone = view.findViewById(R.id.etDialogPhone);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveEdit);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelEdit);

        if (user != null) {
            etName.setText(user.getName());
            etPhone.setText(user.getPhone());
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();

        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            updateProfile(name, phone);
        });

        return dialog;
    }

    private void updateProfile(String name, String phone) {
        user.setName(name);
        user.setPhone(phone);

        new UserRepository().updateUser(user, new UserRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                if (listener != null) listener.onProfileUpdated();
                dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
