package com.example.projmate.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project implements Serializable {
    private String projectId;
    private String name;
    private String description;
    private String imageUri; // Store as String for local storage
    private String projectLink;
    private String ownerId;
    private List<String> interestedUserIds;
    private List<String> starredByUserIds;
    private transient Bitmap imageBitmap; // Not serialized, used for UI

    // Default constructor required for local storage
    public Project() {
        this.projectId = UUID.randomUUID().toString();
        this.interestedUserIds = new ArrayList<>();
        this.starredByUserIds = new ArrayList<>();
    }

    public Project(String name, String description, String imageUri, String projectLink, String ownerId) {
        this.projectId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
        this.projectLink = projectLink;
        this.ownerId = ownerId;
        this.interestedUserIds = new ArrayList<>();
        this.starredByUserIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getProjectLink() {
        return projectLink;
    }

    public void setProjectLink(String projectLink) {
        this.projectLink = projectLink;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getInterestedUserIds() {
        return interestedUserIds;
    }

    public void setInterestedUserIds(List<String> interestedUserIds) {
        this.interestedUserIds = interestedUserIds;
    }

    public void addInterestedUserId(String userId) {
        if (interestedUserIds == null) {
            interestedUserIds = new ArrayList<>();
        }
        if (!interestedUserIds.contains(userId)) {
            interestedUserIds.add(userId);
        }
    }

    public List<String> getStarredByUserIds() {
        return starredByUserIds;
    }

    public void setStarredByUserIds(List<String> starredByUserIds) {
        this.starredByUserIds = starredByUserIds;
    }

    public void addStarredByUserId(String userId) {
        if (starredByUserIds == null) {
            starredByUserIds = new ArrayList<>();
        }
        if (!starredByUserIds.contains(userId)) {
            starredByUserIds.add(userId);
        }
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}