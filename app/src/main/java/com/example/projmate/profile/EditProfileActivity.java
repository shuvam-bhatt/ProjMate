package com.example.projmate.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projmate.R;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;
import com.example.projmate.utils.SharedPreferencesManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView ivProfileImage;
    private Button btnChangePhoto, btnSaveProfile;
    private TextInputLayout tilName, tilPhoneNumber, tilEmail;
    private TextInputEditText etName, etPhoneNumber, etEmail;
    private ImageButton btnBack;

    private Uri selectedImageUri = null;
    private LocalStorageUtil storageUtil;
    private SharedPreferencesManager prefsManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize storage utilities
        storageUtil = LocalStorageUtil.getInstance(this);
        prefsManager = new SharedPreferencesManager(this);
        
        // Initialize views
        initViews();
        
        // Load current user data
        loadUserData();
        
        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btnBack);
        
        tilName = findViewById(R.id.tilName);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tilEmail = findViewById(R.id.tilEmail);
        
        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
    }

    private void loadUserData() {
        // Get current user from LocalStorageUtil
        currentUser = storageUtil.getCurrentUser();
        
        if (currentUser != null) {
            // Set existing user data to form fields
            etName.setText(currentUser.getName());
            etEmail.setText(currentUser.getEmail());
            
            // Set phone number if available
            if (currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty()) {
                etPhoneNumber.setText(currentUser.getPhoneNumber());
            }
            
            // Load profile image if available
            if (currentUser.getProfileImageUri() != null && !currentUser.getProfileImageUri().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(currentUser.getProfileImageUri());
                    ivProfileImage.setImageURI(imageUri);
                    selectedImageUri = imageUri;
                } catch (Exception e) {
                    // If there's an error loading the image, use the default
                    ivProfileImage.setImageResource(R.drawable.default_profile_image);
                }
            }
        }
    }

    private void setClickListeners() {
        // Back button click listener
        btnBack.setOnClickListener(v -> finish());
        
        // Change photo button click listener
        btnChangePhoto.setOnClickListener(v -> openImagePicker());
        
        // Save profile button click listener
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivProfileImage.setImageURI(selectedImageUri);
        }
    }

    private void saveProfile() {
        // Validate input fields
        if (!validateInputs()) {
            return;
        }
        
        // Get values from input fields
        String name = etName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        
        // Update user object
        if (currentUser == null) {
            currentUser = new User();
        }
        
        currentUser.setName(name);
        currentUser.setPhoneNumber(phoneNumber);
        currentUser.setEmail(email);
        
        // Set profile image URI if selected
        if (selectedImageUri != null) {
            currentUser.setProfileImageUri(selectedImageUri.toString());
        }
        
        // Save updated user to both storage systems
        storageUtil.saveUser(currentUser);
        prefsManager.saveUser(currentUser);
        
        // Show success message
        Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
        
        // Return to previous screen
        finish();
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate name
        if (etName.getText().toString().trim().isEmpty()) {
            tilName.setError("Name cannot be empty");
            isValid = false;
        } else {
            tilName.setError(null);
        }
        
        // Validate email
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }
        
        return isValid;
    }
}