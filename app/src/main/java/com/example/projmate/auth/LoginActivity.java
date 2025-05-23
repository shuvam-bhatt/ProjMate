package com.example.projmate.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projmate.MainActivity;
import com.example.projmate.R;
import com.example.projmate.model.User;
import com.example.projmate.onboarding.FieldOfInterestActivity;
import com.example.projmate.util.LocalStorageUtil;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgotPassword;
    private LocalStorageUtil storageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        storageUtil = LocalStorageUtil.getInstance(this);

        // Auto-login with demo account for testing
        autoLoginWithDemoAccount();

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        
        // Pre-fill demo account credentials
        etEmail.setText("john@example.com");
        etPassword.setText("password1");
        
        // Show available demo accounts
        Toast.makeText(this, "Demo accounts available:\n" +
                "john@example.com / password1\n" +
                "jane@example.com / password2\n" +
                "alex@example.com / password3", Toast.LENGTH_LONG).show();

        // Set click listeners
        btnLogin.setOnClickListener(v -> loginUser());
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
        tvForgotPassword.setOnClickListener(v -> {
            // In a real app, you would implement password recovery
            Toast.makeText(LoginActivity.this, "Password recovery not implemented in this demo", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Auto-login with demo account for testing purposes
     */
    private void autoLoginWithDemoAccount() {
        // We'll skip auto-login to allow manual login with any account
        // Just pre-fill the fields with demo account credentials
    }
    
    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        // Attempt login
        User user = storageUtil.loginUser(email, password);
        if (user != null) {
            // Login successful
            // Navigate to the Field of Interest selection screen
            startActivity(new Intent(LoginActivity.this, FieldOfInterestActivity.class));
            finish();
        } else {
            // Login failed
            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}