package com.example.quickcommercedemo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcommercedemo.MainActivity;
import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.models.User;
import com.example.quickcommercedemo.repositories.UserRepository;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.example.quickcommercedemo.utils.SessionManager;
import com.example.quickcommercedemo.utils.ValidationUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, emailLayout, phoneLayout, addressLayout, passwordLayout, confirmPasswordLayout;
    private EditText nameInput, emailInput, phoneInput, addressInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;

    private FirebaseAuth auth;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        auth = DatabaseManager.getInstance().getAuth();
        userRepository = new UserRepository();
        sessionManager = new SessionManager(this);

        registerButton.setOnClickListener(v -> validateAndRegister());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void initializeViews() {
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        addressLayout = findViewById(R.id.addressLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);
    }

    private void validateAndRegister() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        nameLayout.setError(null);
        emailLayout.setError(null);
        phoneLayout.setError(null);
        addressLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        boolean isValid = true;

        if (!ValidationUtil.isValidName(name)) {
            nameLayout.setError("Name must be at least 2 characters");
            isValid = false;
        }

        String emailError = ValidationUtil.getEmailErrorMessage(email);
        if (emailError != null) {
            emailLayout.setError(emailError);
            isValid = false;
        }

        String phoneError = ValidationUtil.getPhoneErrorMessage(phone);
        if (phoneError != null) {
            phoneLayout.setError(phoneError);
            isValid = false;
        }

        if (!ValidationUtil.isValidAddress(address)) {
            addressLayout.setError("Address must be at least 5 characters");
            isValid = false;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        }

        if (isValid) {
            performRegistration(name, email, phone, address, password);
        }
    }

    private void performRegistration(String name, String email, String phone, String address, String password) {
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                String userId = authResult.getUser().getUid();
                createUserDocument(userId, name, email, phone, address);
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void createUserDocument(String userId, String name, String email, String phone, String address) {
        User user = new User(userId, name, email, phone, address);

        userRepository.createUser(user, new UserRepository.VoidCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();

                sessionManager.createLoginSession(userId, name, email, false);

                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Error creating user profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

