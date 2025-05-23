package com.example.projmate.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.projmate.model.Message;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LocalStorageUtil {
    private static final String TAG = "LocalStorageUtil";
    private static final String PREF_NAME = "ProjMatePrefs";
    private static final String CURRENT_USER_ID = "current_user_id";
    private static final String USERS_DIR = "users";
    private static final String PROJECTS_DIR = "projects";
    private static final String MESSAGES_DIR = "messages";
    private static final String IMAGES_DIR = "images";

    private static LocalStorageUtil instance;
    private Context context;
    private SharedPreferences preferences;
    private Map<String, User> userCache = new HashMap<>();
    private Map<String, Project> projectCache = new HashMap<>();
    private List<Message> messageCache = new ArrayList<>();
    private String currentUserId;

    private LocalStorageUtil(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.currentUserId = preferences.getString(CURRENT_USER_ID, null);
        createDirectories();
        loadCaches();
    }

    public static synchronized LocalStorageUtil getInstance(Context context) {
        if (instance == null) {
            instance = new LocalStorageUtil(context);
        }
        return instance;
    }

    private void createDirectories() {
        try {
            File filesDir = context.getFilesDir();
            if (filesDir == null) {
                Log.e(TAG, "Files directory is null");
                return;
            }
            
            Log.d(TAG, "Files directory path: " + filesDir.getAbsolutePath());
            
            File usersDir = new File(filesDir, USERS_DIR);
            File projectsDir = new File(filesDir, PROJECTS_DIR);
            File messagesDir = new File(filesDir, MESSAGES_DIR);
            File imagesDir = new File(filesDir, IMAGES_DIR);

            if (!usersDir.exists()) {
                boolean created = usersDir.mkdirs();
                Log.d(TAG, "Users directory created: " + created);
            }
            if (!projectsDir.exists()) {
                boolean created = projectsDir.mkdirs();
                Log.d(TAG, "Projects directory created: " + created);
            }
            if (!messagesDir.exists()) {
                boolean created = messagesDir.mkdirs();
                Log.d(TAG, "Messages directory created: " + created);
            }
            if (!imagesDir.exists()) {
                boolean created = imagesDir.mkdirs();
                Log.d(TAG, "Images directory created: " + created);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating directories: " + e.getMessage(), e);
        }
    }

    private void loadCaches() {
        loadUsers();
        loadProjects();
        loadMessages();
    }

    // User methods
    private void loadUsers() {
        try {
            File usersDir = new File(context.getFilesDir(), USERS_DIR);
            if (!usersDir.exists()) {
                Log.d(TAG, "Users directory does not exist, creating it");
                usersDir.mkdirs();
                return;
            }
            
            File[] userFiles = usersDir.listFiles();
            if (userFiles == null) {
                Log.d(TAG, "No user files found");
                return;
            }
            
            Log.d(TAG, "Found " + userFiles.length + " user files");
            for (File file : userFiles) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    User user = (User) ois.readObject();
                    if (user == null || user.getUserId() == null) {
                        Log.e(TAG, "Invalid user data in file: " + file.getName());
                        continue;
                    }
                    userCache.put(user.getUserId(), user);
                    Log.d(TAG, "Loaded user: " + user.getName() + " (ID: " + user.getUserId() + ")");
                    ois.close();
                    fis.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error loading user from file " + file.getName() + ": " + e.getMessage(), e);
                    // Consider deleting corrupted files
                    // file.delete();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadUsers method: " + e.getMessage(), e);
        }
    }

    public User getCurrentUser() {
        if (currentUserId != null) {
            return getUserById(currentUserId);
        }
        return null;
    }

    public void setCurrentUser(User user) {
        if (user != null) {
            this.currentUserId = user.getUserId();
            preferences.edit().putString(CURRENT_USER_ID, currentUserId).apply();
        } else {
            this.currentUserId = null;
            preferences.edit().remove(CURRENT_USER_ID).apply();
        }
    }

    public User getUserById(String userId) {
        return userCache.get(userId);
    }

    public User getUserByEmail(String email) {
        for (User user : userCache.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public void saveUser(User user) {
        userCache.put(user.getUserId(), user);
        try {
            File file = new File(new File(context.getFilesDir(), USERS_DIR), user.getUserId());
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving user: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userCache.values());
    }

    // Project methods
    private void loadProjects() {
        try {
            File projectsDir = new File(context.getFilesDir(), PROJECTS_DIR);
            if (!projectsDir.exists()) {
                Log.d(TAG, "Projects directory does not exist, creating it");
                projectsDir.mkdirs();
                return;
            }
            
            File[] projectFiles = projectsDir.listFiles();
            if (projectFiles == null) {
                Log.d(TAG, "No project files found");
                return;
            }
            
            Log.d(TAG, "Found " + projectFiles.length + " project files");
            for (File file : projectFiles) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Project project = (Project) ois.readObject();
                    if (project == null || project.getProjectId() == null) {
                        Log.e(TAG, "Invalid project data in file: " + file.getName());
                        continue;
                    }
                    projectCache.put(project.getProjectId(), project);
                    Log.d(TAG, "Loaded project: " + project.getName() + " (ID: " + project.getProjectId() + ")");
                    ois.close();
                    fis.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error loading project from file " + file.getName() + ": " + e.getMessage(), e);
                    // Consider deleting corrupted files
                    // file.delete();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadProjects method: " + e.getMessage(), e);
        }
    }

    public Project getProjectById(String projectId) {
        return projectCache.get(projectId);
    }

    public void saveProject(Project project) {
        projectCache.put(project.getProjectId(), project);
        try {
            File file = new File(new File(context.getFilesDir(), PROJECTS_DIR), project.getProjectId());
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(project);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving project: " + e.getMessage());
        }
    }

    public List<Project> getAllProjects() {
        return new ArrayList<>(projectCache.values());
    }

    public List<Project> getProjectsByUser(String userId) {
        List<Project> userProjects = new ArrayList<>();
        for (Project project : projectCache.values()) {
            if (project.getOwnerId().equals(userId)) {
                userProjects.add(project);
            }
        }
        return userProjects;
    }
    
    public List<Project> getStarredProjectsByUser(String userId) {
        List<Project> starredProjects = new ArrayList<>();
        User user = getUserById(userId);
        
        if (user == null || user.getStarredProjectIds() == null) {
            return starredProjects;
        }
        
        for (String projectId : user.getStarredProjectIds()) {
            Project project = getProjectById(projectId);
            if (project != null) {
                starredProjects.add(project);
            }
        }
        
        return starredProjects;
    }

    public List<Project> getProjectsForDiscovery(String userId) {
        List<Project> discoveryProjects = new ArrayList<>();
        User currentUser = getUserById(userId);
        
        if (currentUser == null) return discoveryProjects;
        
        for (Project project : projectCache.values()) {
            // Don't show user's own projects
            if (project.getOwnerId().equals(userId)) continue;
            
            // Don't show projects user has already interacted with
            if (currentUser.getMatchedProjectIds().contains(project.getProjectId())) continue;
            if (currentUser.getStarredProjectIds().contains(project.getProjectId())) continue;
            
            discoveryProjects.add(project);
        }
        return discoveryProjects;
    }

    // Message methods
    private void loadMessages() {
        try {
            File messagesDir = new File(context.getFilesDir(), MESSAGES_DIR);
            if (!messagesDir.exists()) {
                Log.d(TAG, "Messages directory does not exist, creating it");
                messagesDir.mkdirs();
                return;
            }
            
            File[] messageFiles = messagesDir.listFiles();
            if (messageFiles == null) {
                Log.d(TAG, "No message files found");
                return;
            }
            
            Log.d(TAG, "Found " + messageFiles.length + " message files");
            for (File file : messageFiles) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    Message message = (Message) ois.readObject();
                    if (message == null || message.getMessageId() == null) {
                        Log.e(TAG, "Invalid message data in file: " + file.getName());
                        continue;
                    }
                    messageCache.add(message);
                    Log.d(TAG, "Loaded message: " + message.getMessageId());
                    ois.close();
                    fis.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error loading message from file " + file.getName() + ": " + e.getMessage(), e);
                    // Consider deleting corrupted files
                    // file.delete();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadMessages method: " + e.getMessage(), e);
        }
    }

    public void saveMessage(Message message) {
        messageCache.add(message);
        try {
            File file = new File(new File(context.getFilesDir(), MESSAGES_DIR), message.getMessageId());
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(message);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving message: " + e.getMessage());
        }
    }

    public List<Message> getMessagesBetweenUsers(String userId1, String userId2, String projectId) {
        List<Message> messages = new ArrayList<>();
        for (Message message : messageCache) {
            if (projectId != null && !message.getProjectId().equals(projectId)) continue;
            
            if ((message.getSenderId().equals(userId1) && message.getReceiverId().equals(userId2)) ||
                (message.getSenderId().equals(userId2) && message.getReceiverId().equals(userId1))) {
                messages.add(message);
            }
        }
        return messages;
    }

    public List<Message> getAllMessagesForUser(String userId) {
        List<Message> userMessages = new ArrayList<>();
        for (Message message : messageCache) {
            if (message.getSenderId().equals(userId) || message.getReceiverId().equals(userId)) {
                userMessages.add(message);
            }
        }
        return userMessages;
    }

    // Image methods
    public String saveImageToStorage(Bitmap bitmap) {
        // Resize bitmap if it's too large
        Bitmap resizedBitmap = resizeBitmapIfNeeded(bitmap, 2048, 2048);
        
        String imageId = UUID.randomUUID().toString();
        File imageFile = new File(new File(context.getFilesDir(), IMAGES_DIR), imageId);
        
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();
            
            // If we created a new bitmap, recycle the original to free memory
            if (resizedBitmap != bitmap) {
                bitmap.recycle();
            }
            
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Resize a bitmap if it exceeds the maximum dimensions
     */
    private Bitmap resizeBitmapIfNeeded(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        // If the bitmap is already smaller than the max dimensions, return it as is
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }
        
        // Calculate the scaling factor
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);
        
        // Create a new bitmap with the scaled dimensions
        int newWidth = Math.round(width * scaleFactor);
        int newHeight = Math.round(height * scaleFactor);
        
        Log.d(TAG, "Resizing image from " + width + "x" + height + " to " + newWidth + "x" + newHeight);
        
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }

    public Bitmap loadImageFromStorage(String path) {
        try {
            File imageFile = new File(path);
            
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 1024, 1024);
            
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Calculate an appropriate inSampleSize value for image scaling
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        Log.d(TAG, "Original image size: " + width + "x" + height);
        
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
            
            // For very large images, go beyond powers of 2
            if (height > reqHeight * 4 || width > reqWidth * 4) {
                inSampleSize = Math.max(inSampleSize, Math.min(height / reqHeight, width / reqWidth));
            }
        }
        
        Log.d(TAG, "Using inSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    // Authentication methods
    public User registerUser(String name, String email, String password) {
        // Check if user already exists
        if (getUserByEmail(email) != null) {
            return null;
        }
        
        // Create new user
        User newUser = new User(UUID.randomUUID().toString(), name, email, password);
        saveUser(newUser);
        return newUser;
    }

    public User loginUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            setCurrentUser(user);
            return user;
        }
        return null;
    }

    public void logoutUser() {
        setCurrentUser(null);
    }
}