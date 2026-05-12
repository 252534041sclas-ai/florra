package com.example.florra_a.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_PROFILE_IMAGE = "profile_image";

    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean saveUser(String email, String fullName, String token, boolean isAdmin, String profileImage) {
        return sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_TYPE, isAdmin ? "admin" : "customer")
                .putString(KEY_EMAIL, email)
                .putString(KEY_FULL_NAME, fullName != null ? fullName : (isAdmin ? "Admin User" : "Customer User"))
                .putString(KEY_TOKEN, token)
                .putString(KEY_PROFILE_IMAGE, profileImage)
                .commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, "");
    }

    public boolean isAdmin() {
        return "admin".equals(getUserType());
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, "User");
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public String getProfileImage() {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE, null);
    }

    public void saveProfileImage(String url) {
        sharedPreferences.edit().putString(KEY_PROFILE_IMAGE, url).apply();
    }

    public void logout() {
        sharedPreferences.edit().clear().commit();
    }
}
