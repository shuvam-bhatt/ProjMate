package com.example.projmate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projmate.R;
import com.example.projmate.auth.LoginActivity;
import com.example.projmate.chat.ChatActivity;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;
import com.example.projmate.project.AddProjectActivity;
import com.example.projmate.project.EditProjectActivity;
import com.example.projmate.util.LocalStorageUtil;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvProjectsSubtitle;
    private Button btnAddProject, btnLogout, btnSettings, btnEditProfile, btnMyProjects;
    private CircleImageView ivProfileImage;
    
    private LocalStorageUtil storageUtil;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvProjectsSubtitle = view.findViewById(R.id.tvProjectsSubtitle);
        
        btnAddProject = view.findViewById(R.id.btnAddProject);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnMyProjects = view.findViewById(R.id.btnMyProjects);
        
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        
        // Initialize storage and get current user
        storageUtil = LocalStorageUtil.getInstance(requireContext());
        currentUser = storageUtil.getCurrentUser();
        
        // Set user info
        if (currentUser != null) {
            tvUserName.setText(currentUser.getName());
            tvUserEmail.setText(currentUser.getEmail());
            tvProjectsSubtitle.setText("Projects " + currentUser.getName() + " worked on");
            
            // Load profile image if available
            if (currentUser.getProfileImageUri() != null && !currentUser.getProfileImageUri().isEmpty()) {
                try {
                    android.net.Uri imageUri = android.net.Uri.parse(currentUser.getProfileImageUri());
                    ivProfileImage.setImageURI(imageUri);
                } catch (Exception e) {
                    // If there's an error loading the image, use the default
                    ivProfileImage.setImageResource(R.drawable.default_profile_image);
                }
            }
        }
        
        // Set click listeners
        btnAddProject.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddProjectActivity.class));
        });
        
        btnLogout.setOnClickListener(v -> {
            // Log out the user
            storageUtil.logoutUser();
            
            // Show a confirmation message
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            // Navigate to login screen
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        
        btnSettings.setOnClickListener(v -> {
            // Settings button click handler (empty for now)
        });
        
        btnEditProfile.setOnClickListener(v -> {
            // Launch the EditProfileActivity
            Intent intent = new Intent(requireContext(), com.example.projmate.profile.EditProfileActivity.class);
            startActivity(intent);
        });
        
        btnMyProjects.setOnClickListener(v -> {
            // Launch the ProjectsActivity
            Intent intent = new Intent(requireContext(), com.example.projmate.project.ProjectsActivity.class);
            startActivity(intent);
        });
    }
    

    
    @Override
    public void onResume() {
        super.onResume();
        
        // Get the current user again in case it changed
        currentUser = storageUtil.getCurrentUser();
        
        // Update user info
        if (currentUser != null) {
            tvUserName.setText(currentUser.getName());
            tvUserEmail.setText(currentUser.getEmail());
            tvProjectsSubtitle.setText("Projects " + currentUser.getName() + " worked on");
            
            // Load profile image if available
            if (currentUser.getProfileImageUri() != null && !currentUser.getProfileImageUri().isEmpty()) {
                try {
                    android.net.Uri imageUri = android.net.Uri.parse(currentUser.getProfileImageUri());
                    ivProfileImage.setImageURI(imageUri);
                } catch (Exception e) {
                    // If there's an error loading the image, use the default
                    ivProfileImage.setImageResource(R.drawable.default_profile_image);
                }
            }
        } else {
            // If no user is logged in, go to login screen
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        }
    }
    

}