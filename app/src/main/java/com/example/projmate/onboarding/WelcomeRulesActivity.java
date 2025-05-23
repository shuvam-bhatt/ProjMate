package com.example.projmate.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projmate.MainActivity;
import com.example.projmate.R;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;

public class WelcomeRulesActivity extends AppCompatActivity {

    private Button btnAgree;
    private LocalStorageUtil storageUtil;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_rules);

        // Initialize storage and get current user
        storageUtil = LocalStorageUtil.getInstance(this);
        currentUser = storageUtil.getCurrentUser();

        // Initialize views
        btnAgree = findViewById(R.id.btnAgree);

        // Set click listener for agree button
        btnAgree.setOnClickListener(v -> {
            // Mark onboarding as completed
            markOnboardingCompleted();
            
            // Navigate to main activity
            Intent intent = new Intent(WelcomeRulesActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void markOnboardingCompleted() {
        // In a real app, you would mark onboarding as completed in user preferences
        // For this demo, we'll just proceed to the main activity
    }
}