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

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvLogin;
    private LocalStorageUtil storageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        storageUtil = LocalStorageUtil.getInstance(this);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);

        // Set click listeners
        btnSignUp.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> {
            finish(); // Go back to login activity
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Attempt registration
        User user = storageUtil.registerUser(name, email, password);
        if (user != null) {
            // Registration successful, automatically log in
            storageUtil.setCurrentUser(user);
            // Navigate to the Field of Interest selection screen
            startActivity(new Intent(SignUpActivity.this, FieldOfInterestActivity.class));
            finish();
        } else {
            // Registration failed
            Toast.makeText(SignUpActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
        }
    }
}