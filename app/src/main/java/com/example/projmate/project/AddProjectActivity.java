package com.example.projmate.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projmate.R;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class AddProjectActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Toolbar toolbar;
    private ImageView ivProjectImage;
    private Button btnUploadImage, btnSaveProject;
    private TextInputEditText etProjectName, etProjectDescription, etProjectLink;
    
    private LocalStorageUtil storageUtil;
    private User currentUser;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        
        // Initialize storage and get current user
        storageUtil = LocalStorageUtil.getInstance(this);
        currentUser = storageUtil.getCurrentUser();
        
        if (currentUser == null) {
            finish();
            return;
        }
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        ivProjectImage = findViewById(R.id.ivProjectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSaveProject = findViewById(R.id.btnSaveProject);
        etProjectName = findViewById(R.id.etProjectName);
        etProjectDescription = findViewById(R.id.etProjectDescription);
        etProjectLink = findViewById(R.id.etProjectLink);
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Set click listeners
        btnUploadImage.setOnClickListener(v -> openImagePicker());
        btnSaveProject.setOnClickListener(v -> saveProject());
    }
    
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Project Image"), PICK_IMAGE_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivProjectImage.setImageBitmap(selectedImageBitmap);
                btnUploadImage.setText("Change Image");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void saveProject() {
        String name = etProjectName.getText().toString().trim();
        String description = etProjectDescription.getText().toString().trim();
        String link = etProjectLink.getText().toString().trim();
        
        // Validate input
        if (TextUtils.isEmpty(name)) {
            etProjectName.setError("Project name is required");
            return;
        }
        
        if (TextUtils.isEmpty(description)) {
            etProjectDescription.setError("Project description is required");
            return;
        }
        
        // Save image if selected
        String imageUri = null;
        if (selectedImageBitmap != null) {
            imageUri = storageUtil.saveImageToStorage(selectedImageBitmap);
        }
        
        // Create and save project
        Project project = new Project(name, description, imageUri, link, currentUser.getUserId());
        storageUtil.saveProject(project);
        
        // Add project to user's projects
        currentUser.addProjectId(project.getProjectId());
        storageUtil.saveUser(currentUser);
        
        Toast.makeText(this, "Project saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}