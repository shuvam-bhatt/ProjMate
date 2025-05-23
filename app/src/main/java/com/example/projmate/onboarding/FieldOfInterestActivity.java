package com.example.projmate.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projmate.MainActivity;
import com.example.projmate.R;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;

import java.util.ArrayList;
import java.util.List;

public class FieldOfInterestActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvSkip;
    private Button btnContinue;
    private List<TextView> interestFields = new ArrayList<>();
    private List<String> selectedInterests = new ArrayList<>();
    private LocalStorageUtil storageUtil;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_of_interest);

        // Initialize storage and get current user
        storageUtil = LocalStorageUtil.getInstance(this);
        currentUser = storageUtil.getCurrentUser();

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        tvSkip = findViewById(R.id.tvSkip);
        btnContinue = findViewById(R.id.btnContinue);

        // Initialize interest fields
        initializeInterestFields();

        // Set click listeners
        btnBack.setOnClickListener(v -> onBackPressed());

        tvSkip.setOnClickListener(v -> {
            // Skip to welcome rules screen
            startActivity(new Intent(FieldOfInterestActivity.this, WelcomeRulesActivity.class));
            finish();
        });

        btnContinue.setOnClickListener(v -> {
            if (selectedInterests.isEmpty()) {
                Toast.makeText(this, "Please select at least one field of interest", Toast.LENGTH_SHORT).show();
            } else {
                // Save selected interests to user profile
                saveInterests();
                
                // Navigate to welcome rules screen
                startActivity(new Intent(FieldOfInterestActivity.this, WelcomeRulesActivity.class));
                finish();
            }
        });
    }

    private void initializeInterestFields() {
        // Add all interest field TextViews to the list
        interestFields.add(findViewById(R.id.tvAI));
        interestFields.add(findViewById(R.id.tvWebDev));
        interestFields.add(findViewById(R.id.tvRobotics));
        interestFields.add(findViewById(R.id.tvMobileDev));
        interestFields.add(findViewById(R.id.tvDataScience));
        interestFields.add(findViewById(R.id.tvCyberSecurity));
        interestFields.add(findViewById(R.id.tvGameDev));
        interestFields.add(findViewById(R.id.tvBlockchain));
        interestFields.add(findViewById(R.id.tvIoT));
        interestFields.add(findViewById(R.id.tvCloudComputing));
        interestFields.add(findViewById(R.id.tvDevOps));
        interestFields.add(findViewById(R.id.tvUiUx));

        // Set click listeners for each interest field
        for (TextView field : interestFields) {
            field.setOnClickListener(v -> toggleInterestSelection(field));
        }
    }

    private void toggleInterestSelection(TextView field) {
        // Toggle selection state
        field.setSelected(!field.isSelected());
        
        String interest = field.getText().toString();
        
        // Update selected interests list
        if (field.isSelected()) {
            if (!selectedInterests.contains(interest)) {
                selectedInterests.add(interest);
            }
        } else {
            selectedInterests.remove(interest);
        }
    }

    private void saveInterests() {
        // In a real app, you would save these to the user's profile
        // For this demo, we'll just show a toast with the selected interests
        StringBuilder interests = new StringBuilder("Selected interests: ");
        for (int i = 0; i < selectedInterests.size(); i++) {
            interests.append(selectedInterests.get(i));
            if (i < selectedInterests.size() - 1) {
                interests.append(", ");
            }
        }
        
        Toast.makeText(this, interests.toString(), Toast.LENGTH_LONG).show();
    }
}