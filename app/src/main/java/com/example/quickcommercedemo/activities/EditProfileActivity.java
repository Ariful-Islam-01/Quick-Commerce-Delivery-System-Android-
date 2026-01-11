package com.example.quickcommercedemo.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.User;
import com.example.quickcommercedemo.repositories.UserRepository;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etAddress;
    private MaterialButton btnSave;
    
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userRepository = new UserRepository();
        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.etEditName);
        etPhone = findViewById(R.id.etEditPhone);
        etAddress = findViewById(R.id.etEditAddress);
        btnSave = findViewById(R.id.btnSaveProfile);

        loadCurrentUserData();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentUserData() {
        userRepository.getUserById(sessionManager.getUserId(), new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser = user;
                etName.setText(user.getName());
                etPhone.setText(user.getPhone());
                etAddress.setText(user.getDefaultAddress());
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setName(name);
        currentUser.setPhone(phone);
        currentUser.setDefaultAddress(address);

        userRepository.updateUser(currentUser, new UserRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                sessionManager.createLoginSession(currentUser.getUserId(), name, currentUser.getEmail(), currentUser.isAdmin());
                sessionManager.setUserAddress(address);
                Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
