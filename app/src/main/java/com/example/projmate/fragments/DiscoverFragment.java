package com.example.projmate.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.projmate.R;
import com.example.projmate.chat.ChatActivity;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DiscoverFragment extends Fragment {

    private CardView cardProject;
    private ImageView ivProjectImage;
    private TextView tvProjectName, tvProjectOwner, tvProjectDescription, tvNoProjects;
    private FloatingActionButton fabReject, fabStar, fabAccept;
    private Button btnMessageOwner;
    
    private LocalStorageUtil storageUtil;
    private User currentUser;
    private List<Project> projectsToDiscover;
    private int currentProjectIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        cardProject = view.findViewById(R.id.cardProject);
        ivProjectImage = view.findViewById(R.id.ivProjectImage);
        tvProjectName = view.findViewById(R.id.tvProjectName);
        tvProjectOwner = view.findViewById(R.id.tvProjectOwner);
        tvProjectDescription = view.findViewById(R.id.tvProjectDescription);
        tvNoProjects = view.findViewById(R.id.tvNoProjects);
        fabReject = view.findViewById(R.id.fabReject);
        fabStar = view.findViewById(R.id.fabStar);
        fabAccept = view.findViewById(R.id.fabAccept);
        btnMessageOwner = view.findViewById(R.id.btnMessageOwner);
        
        // Initialize storage and get current user
        storageUtil = LocalStorageUtil.getInstance(requireContext());
        currentUser = storageUtil.getCurrentUser();
        
        // Get projects for discovery
        loadProjects();
        
        // Set click listeners
        fabReject.setOnClickListener(v -> rejectProject());
        fabStar.setOnClickListener(v -> starProject());
        fabAccept.setOnClickListener(v -> acceptProject());
        btnMessageOwner.setOnClickListener(v -> messageProjectOwner());
        
        // Show first project
        showNextProject();
    }
    
    private void loadProjects() {
        projectsToDiscover = storageUtil.getProjectsForDiscovery(currentUser.getUserId());
        currentProjectIndex = 0;
    }
    
    private void showNextProject() {
        if (projectsToDiscover.isEmpty() || currentProjectIndex >= projectsToDiscover.size()) {
            // No more projects to show
            cardProject.setVisibility(View.GONE);
            tvNoProjects.setVisibility(View.VISIBLE);
            return;
        }
        
        // Show current project
        Project project = projectsToDiscover.get(currentProjectIndex);
        User projectOwner = storageUtil.getUserById(project.getOwnerId());
        
        tvProjectName.setText(project.getName());
        tvProjectOwner.setText("by " + (projectOwner != null ? projectOwner.getName() : "Unknown"));
        tvProjectDescription.setText(project.getDescription());
        
        // Load project image if available
        if (project.getImageUri() != null && !project.getImageUri().isEmpty()) {
            try {
                // Check if image is already cached in the project object
                if (project.getImageBitmap() != null) {
                    // Use cached bitmap
                    ivProjectImage.setImageBitmap(project.getImageBitmap());
                } else {
                    // Load the image from storage with safe handling
                    try {
                        Bitmap bitmap = storageUtil.loadImageFromStorage(project.getImageUri());
                        if (bitmap != null) {
                            // Cache the bitmap in the project object
                            project.setImageBitmap(bitmap);
                            // Set the image to the ImageView
                            ivProjectImage.setImageBitmap(bitmap);
                        } else {
                            // If loading fails, use placeholder
                            ivProjectImage.setImageResource(R.drawable.placeholder_project);
                        }
                    } catch (OutOfMemoryError oom) {
                        // Handle out of memory errors specifically
                        Log.e("DiscoverFragment", "Out of memory error loading image: " + oom.getMessage());
                        ivProjectImage.setImageResource(R.drawable.placeholder_project);
                        // Clear any references to large bitmaps
                        System.gc();
                    }
                }
            } catch (Exception e) {
                // Handle any exceptions during image loading
                Log.e("DiscoverFragment", "Error loading image: " + e.getMessage(), e);
                ivProjectImage.setImageResource(R.drawable.placeholder_project);
            }
        } else {
            // No image URI, use placeholder
            ivProjectImage.setImageResource(R.drawable.placeholder_project);
        }
        
        cardProject.setVisibility(View.VISIBLE);
        tvNoProjects.setVisibility(View.GONE);
    }
    
    private void rejectProject() {
        if (projectsToDiscover.isEmpty() || currentProjectIndex >= projectsToDiscover.size()) {
            return;
        }
        
        // Simply move to next project
        currentProjectIndex++;
        showNextProject();
    }
    
    private void starProject() {
        if (projectsToDiscover.isEmpty() || currentProjectIndex >= projectsToDiscover.size()) {
            return;
        }
        
        Project project = projectsToDiscover.get(currentProjectIndex);
        
        // Add to starred projects
        currentUser.addStarredProjectId(project.getProjectId());
        project.addStarredByUserId(currentUser.getUserId());
        
        // Save changes
        storageUtil.saveUser(currentUser);
        storageUtil.saveProject(project);
        
        Toast.makeText(requireContext(), "Project starred!", Toast.LENGTH_SHORT).show();
        
        // Move to next project
        currentProjectIndex++;
        showNextProject();
    }
    
    private void acceptProject() {
        if (projectsToDiscover.isEmpty() || currentProjectIndex >= projectsToDiscover.size()) {
            return;
        }
        
        Project project = projectsToDiscover.get(currentProjectIndex);
        
        // Add to matched projects
        currentUser.addMatchedProjectId(project.getProjectId());
        project.addInterestedUserId(currentUser.getUserId());
        
        // Save changes
        storageUtil.saveUser(currentUser);
        storageUtil.saveProject(project);
        
        // Show toast message
        Toast.makeText(requireContext(), "Project accepted!", Toast.LENGTH_SHORT).show();
        
        // Move to next project
        currentProjectIndex++;
        showNextProject();
    }
    
    /**
     * Open chat with the project owner
     */
    private void messageProjectOwner() {
        if (projectsToDiscover.isEmpty() || currentProjectIndex >= projectsToDiscover.size()) {
            return;
        }
        
        Project project = projectsToDiscover.get(currentProjectIndex);
        
        // Open chat with project owner
        Intent chatIntent = new Intent(requireContext(), ChatActivity.class);
        chatIntent.putExtra("PROJECT_ID", project.getProjectId());
        chatIntent.putExtra("RECEIVER_ID", project.getOwnerId());
        startActivity(chatIntent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload projects in case something changed
        loadProjects();
        showNextProject();
    }
}