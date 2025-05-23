package com.example.projmate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.projmate.model.User;
import com.google.gson.Gson;

/**
 * Utility class to manage SharedPreferences operations
 */
public class SharedPreferencesManager {

    
    private static final String PREF_NAME = "ProjMatePrefs";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Save user object to SharedPreferences
     * @param user User object to save
     */
    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    /**
     * Get user object from SharedPreferences
     * @return User object or null if not found
     */
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    /**
     * Set login status
     * @param isLoggedIn true if user is logged in, false otherwise
     */
    public void setLoggedIn(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clear all data from SharedPreferences
     */
    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}