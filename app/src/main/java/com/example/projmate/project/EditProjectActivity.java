package com.example.projmate.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

public class EditProjectActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Toolbar toolbar;
    private ImageView ivProjectImage;
    private Button btnUploadImage, btnSaveProject;
    private TextInputEditText etProjectName, etProjectDescription, etProjectLink;
    
    private LocalStorageUtil storageUtil;
    private User currentUser;
    private Project project;
    private Bitmap selectedImageBitmap;
    private String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        
        // Get project ID from intent
        projectId = getIntent().getStringExtra("PROJECT_ID");
        if (projectId == null) {
            finish();
            return;
        }
        
        // Initialize storage and get current user and project
        storageUtil = LocalStorageUtil.getInstance(this);
        currentUser = storageUtil.getCurrentUser();
        project = storageUtil.getProjectById(projectId);
        
        if (currentUser == null || project == null) {
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
        toolbar.setTitle("Edit Project");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Fill in project details
        etProjectName.setText(project.getName());
        etProjectDescription.setText(project.getDescription());
        etProjectLink.setText(project.getProjectLink());
        
        // Load project image if available
        if (project.getImageUri() != null && !project.getImageUri().isEmpty()) {
            try {
                // Check if image is already cached in the project object
                if (project.getImageBitmap() != null) {
                    // Use cached bitmap
                    ivProjectImage.setImageBitmap(project.getImageBitmap());
                    btnUploadImage.setText("Change Image");
                } else {
                    // Load the image from storage with safe handling
                    try {
                        Bitmap bitmap = storageUtil.loadImageFromStorage(project.getImageUri());
                        if (bitmap != null) {
                            // Cache the bitmap in the project object
                            project.setImageBitmap(bitmap);
                            // Set the image to the ImageView
                            ivProjectImage.setImageBitmap(bitmap);
                            btnUploadImage.setText("Change Image");
                        } else {
                            // If loading fails, use placeholder
                            ivProjectImage.setImageResource(R.drawable.placeholder_project);
                        }
                    } catch (OutOfMemoryError oom) {
                        // Handle out of memory errors specifically
                        Log.e("EditProjectActivity", "Out of memory error loading image: " + oom.getMessage());
                        // Clear any references to large bitmaps
                        System.gc();
                        // Use placeholder when OOM occurs
                        ivProjectImage.setImageResource(R.drawable.placeholder_project);
                    }
                }
            } catch (Exception e) {
                // Handle any exceptions during image loading
                e.printStackTrace();
                ivProjectImage.setImageResource(R.drawable.placeholder_project);
            }
        }
        
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
        if (selectedImageBitmap != null) {
            String imageUri = storageUtil.saveImageToStorage(selectedImageBitmap);
            project.setImageUri(imageUri);
        }
        
        // Update project details
        project.setName(name);
        project.setDescription(description);
        project.setProjectLink(link);
        
        // Save changes
        storageUtil.saveProject(project);
        
        Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
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