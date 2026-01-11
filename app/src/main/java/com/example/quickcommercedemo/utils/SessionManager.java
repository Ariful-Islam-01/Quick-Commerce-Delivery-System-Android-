package com.example.quickcommercedemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "QuickCommerceSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IS_ADMIN = "isAdmin";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_ADDRESS = "userAddress";
    private static final String KEY_DARK_MODE = "darkMode";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void createLoginSession(String userId, String userName, String userEmail, boolean isAdmin) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putBoolean(KEY_IS_ADMIN, isAdmin);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void setUserPhone(String phone) {
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public String getUserPhone() {
        return preferences.getString(KEY_USER_PHONE, "");
    }

    public void setUserAddress(String address) {
        editor.putString(KEY_USER_ADDRESS, address);
        editor.apply();
    }

    public String getUserAddress() {
        return preferences.getString(KEY_USER_ADDRESS, "");
    }

    public void setDarkMode(boolean isEnabled) {
        editor.putBoolean(KEY_DARK_MODE, isEnabled);
        editor.apply();
    }

    public boolean isDarkMode() {
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    public boolean isAdmin() {
        return preferences.getBoolean(KEY_IS_ADMIN, false);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
