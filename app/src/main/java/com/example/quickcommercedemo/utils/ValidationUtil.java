package com.example.quickcommercedemo.utils;

import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Email validation
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Phone validation (Bangladesh format)
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        // Bangladesh phone: 11 digits starting with 01
        Pattern pattern = Pattern.compile("^01[0-9]{9}$");
        return pattern.matcher(phone.trim()).matches();
    }

    // Password strength validation
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        // Minimum 6 characters
        return password.length() >= 6;
    }

    // Strong password validation (optional)
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }

    // Name validation
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }

    // Address validation
    public static boolean isValidAddress(String address) {
        return address != null && address.trim().length() >= 5;
    }

    // Delivery fee validation
    public static boolean isValidDeliveryFee(String feeStr) {
        try {
            double fee = Double.parseDouble(feeStr);
            return fee > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // General non-empty validation
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    // Get password strength message
    public static String getPasswordStrengthMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        if (!isStrongPassword(password)) {
            return "Use uppercase, lowercase, and numbers for stronger password";
        }
        return "Strong password";
    }

    // Get email error message
    public static String getEmailErrorMessage(String email) {
        if (email == null || email.isEmpty()) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }
        return null;
    }

    // Get phone error message
    public static String getPhoneErrorMessage(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "Phone number is required";
        }
        if (!isValidPhone(phone)) {
            return "Please enter valid BD phone (01XXXXXXXXX)";
        }
        return null;
    }
}

