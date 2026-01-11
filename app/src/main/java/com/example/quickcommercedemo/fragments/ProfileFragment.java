package com.example.quickcommercedemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.quickcommercedemo.R;
import com.example.quickcommercedemo.activities.LoginActivity;
import com.example.quickcommercedemo.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail;
    private SwitchMaterial switchTheme;
    private MaterialButton btnLogout;
    private View btnEditProfile, btnChangeAddress;
    
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        switchTheme = view.findViewById(R.id.switchTheme);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangeAddress = view.findViewById(R.id.btnChangeAddress);

        loadUserData();
        setupListeners();

        return view;
    }

    private void loadUserData() {
        tvProfileName.setText(sessionManager.getUserName());
        tvProfileEmail.setText(sessionManager.getUserEmail());
        
        // Handle theme switch state
        boolean isDarkMode = sessionManager.isDarkMode();
        switchTheme.setChecked(isDarkMode);
    }

    private void setupListeners() {
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        btnLogout.setOnClickListener(v -> logout());

        btnEditProfile.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Edit Profile coming soon", Toast.LENGTH_SHORT).show());
            
        btnChangeAddress.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Address Management coming soon", Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logoutUser();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
