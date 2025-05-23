package com.example.projmate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String userId;
    private String name;
    private String email;
    private String password; // Note: In a real app, you'd never store plain text passwords
    private String phoneNumber;
    private String profileImageUri;
    private List<String> projectIds;
    private List<String> starredProjectIds;
    private List<String> matchedProjectIds;

    // Default constructor required for local storage
    public User() {
        projectIds = new ArrayList<>();
        starredProjectIds = new ArrayList<>();
        matchedProjectIds = new ArrayList<>();
    }

    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.projectIds = new ArrayList<>();
        this.starredProjectIds = new ArrayList<>();
        this.matchedProjectIds = new ArrayList<>();
    }
    
    public User(String userId, String name, String email, String password, String phoneNumber, String profileImageUri) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImageUri = profileImageUri;
        this.projectIds = new ArrayList<>();
        this.starredProjectIds = new ArrayList<>();
        this.matchedProjectIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<String> projectIds) {
        this.projectIds = projectIds;
    }

    public void addProjectId(String projectId) {
        if (projectIds == null) {
            projectIds = new ArrayList<>();
        }
        if (!projectIds.contains(projectId)) {
            projectIds.add(projectId);
        }
    }

    public List<String> getStarredProjectIds() {
        return starredProjectIds;
    }

    public void setStarredProjectIds(List<String> starredProjectIds) {
        this.starredProjectIds = starredProjectIds;
    }

    public void addStarredProjectId(String projectId) {
        if (starredProjectIds == null) {
            starredProjectIds = new ArrayList<>();
        }
        if (!starredProjectIds.contains(projectId)) {
            starredProjectIds.add(projectId);
        }
    }

    public List<String> getMatchedProjectIds() {
        return matchedProjectIds;
    }

    public void setMatchedProjectIds(List<String> matchedProjectIds) {
        this.matchedProjectIds = matchedProjectIds;
    }

    public void addMatchedProjectId(String projectId) {
        if (matchedProjectIds == null) {
            matchedProjectIds = new ArrayList<>();
        }
        if (!matchedProjectIds.contains(projectId)) {
            matchedProjectIds.add(projectId);
        }
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }
}