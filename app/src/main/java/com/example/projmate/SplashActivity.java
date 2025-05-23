package com.example.projmate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projmate.auth.LoginActivity;
import com.example.projmate.util.DemoDataUtil;
import com.example.projmate.util.LocalStorageUtil;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using the splash_background.xml as the background in the theme
        
        try {
            // Initialize LocalStorageUtil early to catch any exceptions
            LocalStorageUtil storageUtil = LocalStorageUtil.getInstance(this);
            
            // Initialize demo data
            DemoDataUtil.createDemoData(this);
            Log.d(TAG, "Demo data initialized");
            
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    // Check if user is already logged in
                    if (storageUtil.getCurrentUser() != null) {
                        Log.d(TAG, "User is logged in, starting MainActivity");
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        Log.d(TAG, "User is not logged in, starting LoginActivity");
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error in splash delay handler: " + e.getMessage(), e);
                    // Don't show error message to user, just log it
                    // Fall back to login activity
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, SPLASH_DELAY);
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            // Don't show error message to user, just log it
            // Fall back to login activity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}