package com.example.projmate.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projmate.R;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;

import java.util.List;

public class ProjectsActivity extends AppCompatActivity {

    private TextView tvMyProjectsTitle, tvStarredProjectsTitle;
    private TextView tvNoMyProjects, tvNoStarredProjects;
    private RecyclerView rvMyProjects, rvStarredProjects;
    private ImageButton btnBack;
    
    private LocalStorageUtil storageUtil;
    private User currentUser;
    private ProjectAdapter myProjectsAdapter, starredProjectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        
        // Initialize views
        tvMyProjectsTitle = findViewById(R.id.tvMyProjectsTitle);
        tvStarredProjectsTitle = findViewById(R.id.tvStarredProjectsTitle);
        tvNoMyProjects = findViewById(R.id.tvNoMyProjects);
        tvNoStarredProjects = findViewById(R.id.tvNoStarredProjects);
        rvMyProjects = findViewById(R.id.rvMyProjects);
        rvStarredProjects = findViewById(R.id.rvStarredProjects);
        btnBack = findViewById(R.id.btnBack);
        
        // Set up RecyclerViews
        rvMyProjects.setLayoutManager(new LinearLayoutManager(this));
        rvStarredProjects.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize storage and get current user
        storageUtil = LocalStorageUtil.getInstance(this);
        currentUser = storageUtil.getCurrentUser();
        
        // Load projects
        if (currentUser != null) {
            loadMyProjects();
            loadStarredProjects();
        }
        
        // Set click listener for back button
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void loadMyProjects() {
        List<Project> myProjects = storageUtil.getProjectsByUser(currentUser.getUserId());
        
        if (myProjects.isEmpty()) {
            rvMyProjects.setVisibility(View.GONE);
            tvNoMyProjects.setVisibility(View.VISIBLE);
        } else {
            rvMyProjects.setVisibility(View.VISIBLE);
            tvNoMyProjects.setVisibility(View.GONE);
            
            myProjectsAdapter = new ProjectAdapter(myProjects);
            rvMyProjects.setAdapter(myProjectsAdapter);
        }
    }
    
    private void loadStarredProjects() {
        List<Project> starredProjects = storageUtil.getStarredProjectsByUser(currentUser.getUserId());
        
        if (starredProjects.isEmpty()) {
            rvStarredProjects.setVisibility(View.GONE);
            tvNoStarredProjects.setVisibility(View.VISIBLE);
        } else {
            rvStarredProjects.setVisibility(View.VISIBLE);
            tvNoStarredProjects.setVisibility(View.GONE);
            
            starredProjectsAdapter = new ProjectAdapter(starredProjects);
            rvStarredProjects.setAdapter(starredProjectsAdapter);
        }
    }
    
    // Project adapter for RecyclerView
    private class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
        
        private List<Project> projects;
        
        public ProjectAdapter(List<Project> projects) {
            this.projects = projects;
        }
        
        @Override
        public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_project, parent, false);
            return new ProjectViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ProjectViewHolder holder, int position) {
            Project project = projects.get(position);
            holder.bind(project);
        }
        
        @Override
        public int getItemCount() {
            return projects.size();
        }
        
        class ProjectViewHolder extends RecyclerView.ViewHolder {
            
            private android.widget.ImageView ivProjectImage;
            private TextView tvProjectName, tvProjectDescription, tvStarCount, tvMatchCount;
            private ImageButton btnEditProject, btnMessageOwner;
            
            public ProjectViewHolder(View itemView) {
                super(itemView);
                
                ivProjectImage = itemView.findViewById(R.id.ivProjectImage);
                tvProjectName = itemView.findViewById(R.id.tvProjectName);
                tvProjectDescription = itemView.findViewById(R.id.tvProjectDescription);
                tvStarCount = itemView.findViewById(R.id.tvStarCount);
                tvMatchCount = itemView.findViewById(R.id.tvMatchCount);
                btnEditProject = itemView.findViewById(R.id.btnEditProject);
                btnMessageOwner = itemView.findViewById(R.id.btnMessageOwner);
            }
            
            public void bind(Project project) {
                tvProjectName.setText(project.getName());
                tvProjectDescription.setText(project.getDescription());
                tvStarCount.setText(String.valueOf(project.getStarredByUserIds().size()));
                tvMatchCount.setText(String.valueOf(project.getInterestedUserIds().size()));
                
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
                                Log.e("ProjectsActivity", "Out of memory error loading image: " + oom.getMessage());
                                ivProjectImage.setImageResource(R.drawable.placeholder_project);
                                // Clear any references to large bitmaps
                                System.gc();
                            }
                        }
                    } catch (Exception e) {
                        // Handle any exceptions during image loading
                        e.printStackTrace();
                        ivProjectImage.setImageResource(R.drawable.placeholder_project);
                    }
                } else {
                    // No image URI, use placeholder
                    ivProjectImage.setImageResource(R.drawable.placeholder_project);
                }
                
                // Set visibility of edit button based on ownership
                if (project.getOwnerId().equals(currentUser.getUserId())) {
                    btnEditProject.setVisibility(View.VISIBLE);
                    btnMessageOwner.setVisibility(View.GONE);
                } else {
                    btnEditProject.setVisibility(View.GONE);
                    btnMessageOwner.setVisibility(View.VISIBLE);
                }
                
                // Set click listener for edit button
                btnEditProject.setOnClickListener(v -> {
                    Intent intent = new Intent(ProjectsActivity.this, EditProjectActivity.class);
                    intent.putExtra("PROJECT_ID", project.getProjectId());
                    startActivity(intent);
                });
                
                // Set click listener for message button
                btnMessageOwner.setOnClickListener(v -> {
                    Intent intent = new Intent(ProjectsActivity.this, com.example.projmate.chat.ChatActivity.class);
                    intent.putExtra("PROJECT_ID", project.getProjectId());
                    intent.putExtra("RECEIVER_ID", project.getOwnerId());
                    startActivity(intent);
                });
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Reload projects in case they were updated
        if (currentUser != null) {
            loadMyProjects();
            loadStarredProjects();
        }
    }
}