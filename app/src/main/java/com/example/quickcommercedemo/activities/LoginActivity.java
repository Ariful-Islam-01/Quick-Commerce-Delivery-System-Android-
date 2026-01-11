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

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.repositories.UserRepository;
import com.example.quickcommercedemo.utils.DatabaseManager;
import com.example.quickcommercedemo.utils.SessionManager;
import com.example.quickcommercedemo.utils.ValidationUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout;
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink, forgotPasswordLink;

    private FirebaseAuth auth;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        auth = DatabaseManager.getInstance().getAuth();
        userRepository = new UserRepository();
        sessionManager = new SessionManager(this);

        loginButton.setOnClickListener(v -> validateAndLogin());
        registerLink.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        forgotPasswordLink.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void initializeViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
    }

    private void validateAndLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        emailLayout.setError(null);
        passwordLayout.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            isValid = false;
        } else if (!ValidationUtil.isValidEmail(email)) {
            emailLayout.setError("Invalid email format");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            isValid = false;
        }

        if (isValid) {
            performLogin(email, password);
        }
    }

    private void performLogin(String email, String password) {
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                String userId = authResult.getUser().getUid();
                fetchUserDataAndProceed(userId);
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void fetchUserDataAndProceed(String userId) {
        userRepository.getUserById(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(com.example.quickcommercedemo.models.User user) {
                progressDialog.dismiss();

                if (user.isBanned()) {
                    auth.signOut();
                    Toast.makeText(LoginActivity.this, "Your account has been banned", Toast.LENGTH_LONG).show();
                    return;
                }

                sessionManager.createLoginSession(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.isAdmin()
                );

                Intent intent;
                if (user.isAdmin()) {
                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, com.example.quickcommercedemo.MainActivity.class);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
    }
}

