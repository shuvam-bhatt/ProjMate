package com.example.projmate.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.projmate.R;
import com.example.projmate.model.Message;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Utility class to generate demo data for the app
 */
public class DemoDataUtil {
    private static final String TAG = "DemoDataUtil";
    
    /**
     * Creates demo users, projects, and messages
     * @param context Application context
     * @return true if demo data was created successfully
     */
    public static boolean createDemoData(Context context) {
        try {
            LocalStorageUtil storageUtil = LocalStorageUtil.getInstance(context);
            
            // Always create demo data for testing purposes
            // Clear existing data first
            Log.d(TAG, "Creating demo data...");
            
            // Create demo users with different passwords for testing
            User user1 = new User(UUID.randomUUID().toString(), "John Doe", "john@example.com", "password1");
            User user2 = new User(UUID.randomUUID().toString(), "Jane Smith", "jane@example.com", "password2");
            User user3 = new User(UUID.randomUUID().toString(), "Alex Johnson", "alex@example.com", "password3");
            User user4 = new User(UUID.randomUUID().toString(), "Maria Garcia", "maria@example.com", "password4");
            User user5 = new User(UUID.randomUUID().toString(), "David Lee", "david@example.com", "password5");
            
            // Save users
            storageUtil.saveUser(user1);
            storageUtil.saveUser(user2);
            storageUtil.saveUser(user3);
            storageUtil.saveUser(user4);
            storageUtil.saveUser(user5);
            
            // Create demo projects with images
            String imageUri1 = loadAndSaveImage(context, R.drawable.ai_image_generator, storageUtil);
            Project project1 = new Project("AI Image Generator", 
                    "A machine learning project that generates realistic images based on text descriptions. Looking for ML engineers and UI designers.", 
                    imageUri1, "https://github.com/johndoe/ai-image-gen", user1.getUserId());
            
            String imageUri2 = loadAndSaveImage(context, R.drawable.fitness_tracker, storageUtil);
            Project project2 = new Project("Fitness Tracker App", 
                    "Mobile app to track workouts, nutrition, and progress. Seeking mobile developers with experience in health apps.", 
                    imageUri2, "https://github.com/janesmith/fitness-tracker", user2.getUserId());
            
            String imageUri3 = loadAndSaveImage(context, R.drawable.smart_home, storageUtil);
            Project project3 = new Project("Smart Home IoT System", 
                    "Building an integrated system for controlling home devices. Need IoT specialists and backend developers.", 
                    imageUri3, "https://github.com/alexj/smart-home", user3.getUserId());
            
            String imageUri4 = loadAndSaveImage(context, R.drawable.e_commerce, storageUtil);
            Project project4 = new Project("E-commerce Platform", 
                    "Full-stack e-commerce solution with payment processing and inventory management. Looking for full-stack developers.", 
                    imageUri4, "https://github.com/mariag/ecommerce", user4.getUserId());
            
            String imageUri5 = loadAndSaveImage(context, R.drawable.blockchain_voting, storageUtil);
            Project project5 = new Project("Blockchain Voting System", 
                    "Secure voting system built on blockchain technology. Need blockchain developers and security experts.", 
                    imageUri5, "https://github.com/davidl/blockchain-vote", user5.getUserId());
            
            String imageUri6 = loadAndSaveImage(context, R.drawable.augmented_reality, storageUtil);
            Project project6 = new Project("Augmented Reality Game", 
                    "Mobile AR game that interacts with real-world environments. Seeking Unity/AR developers and game designers.", 
                    imageUri6, "https://github.com/johndoe/ar-game", user1.getUserId());
            
            String imageUri7 = loadAndSaveImage(context, R.drawable.language_learning, storageUtil);
            Project project7 = new Project("Language Learning Platform", 
                    "Web platform for language learning with AI-powered conversation practice. Need NLP specialists and web developers.", 
                    imageUri7, "https://github.com/janesmith/lang-learn", user2.getUserId());
            
            // Save projects
            storageUtil.saveProject(project1);
            storageUtil.saveProject(project2);
            storageUtil.saveProject(project3);
            storageUtil.saveProject(project4);
            storageUtil.saveProject(project5);
            storageUtil.saveProject(project6);
            storageUtil.saveProject(project7);
            
            // Add projects to user's project list
            user1.addProjectId(project1.getProjectId());
            user1.addProjectId(project6.getProjectId());
            user2.addProjectId(project2.getProjectId());
            user2.addProjectId(project7.getProjectId());
            user3.addProjectId(project3.getProjectId());
            user4.addProjectId(project4.getProjectId());
            user5.addProjectId(project5.getProjectId());
            
            // Save updated users
            storageUtil.saveUser(user1);
            storageUtil.saveUser(user2);
            storageUtil.saveUser(user3);
            storageUtil.saveUser(user4);
            storageUtil.saveUser(user5);
            
            // Create some interactions
            // User2 is interested in User1's project
            user2.addMatchedProjectId(project1.getProjectId());
            project1.addInterestedUserId(user2.getUserId());
            
            // User3 starred User1's project
            user3.addStarredProjectId(project1.getProjectId());
            project1.addStarredByUserId(user3.getUserId());
            
            // User1 is interested in User2's project
            user1.addMatchedProjectId(project2.getProjectId());
            project2.addInterestedUserId(user1.getUserId());
            
            // User4 is interested in User3's project
            user4.addMatchedProjectId(project3.getProjectId());
            project3.addInterestedUserId(user4.getUserId());
            
            // Save updated users and projects
            storageUtil.saveUser(user1);
            storageUtil.saveUser(user2);
            storageUtil.saveUser(user3);
            storageUtil.saveUser(user4);
            storageUtil.saveProject(project1);
            storageUtil.saveProject(project2);
            storageUtil.saveProject(project3);
            
            // Create demo messages
            // Conversation between User1 and User2 about Project1
            createConversation(storageUtil, user1, user2, project1, new String[]{
                "Hi Jane, I noticed you're interested in my AI Image Generator project!",
                "Yes, it looks fascinating! I have experience with ML models for image processing.",
                "That's great! What kind of ML frameworks have you worked with?",
                "Mostly TensorFlow and PyTorch. I've built several CNN models for image classification and generation.",
                "Perfect! That's exactly what we need. Would you be interested in working on the image generation pipeline?",
                "Absolutely! I'd love to help with that part of the project."
            });
            
            // Conversation between User1 and User2 about Project2
            createConversation(storageUtil, user2, user1, project2, new String[]{
                "Hey John, thanks for showing interest in my Fitness Tracker App!",
                "Hi Jane! I really like the concept. I've been wanting to work on a health-related app.",
                "Great! Do you have experience with mobile development?",
                "Yes, I've worked on several Android apps using Java and Kotlin.",
                "Perfect! We're building the app in Kotlin. Would you be interested in working on the UI components?",
                "Definitely! UI design is one of my strengths."
            });
            
            // Conversation between User3 and User4 about Project3
            createConversation(storageUtil, user3, user4, project3, new String[]{
                "Hi Maria, I see you're interested in my Smart Home IoT project!",
                "Yes, it looks like an exciting project! I've been working with IoT devices for a while.",
                "That's great to hear! What kind of IoT experience do you have?",
                "I've worked with Arduino, Raspberry Pi, and various sensors. Also familiar with MQTT for communication.",
                "Perfect! We're using MQTT as well. Would you like to work on the sensor integration part?",
                "I'd love to! That's right up my alley."
            });
            
            Log.d(TAG, "Demo data created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating demo data: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Creates a conversation between two users about a project
     */
    private static void createConversation(LocalStorageUtil storageUtil, User user1, User user2, Project project, String[] messages) {
        long baseTime = new Date().getTime() - (messages.length * 60000); // Start from 'messages.length' minutes ago
        
        for (int i = 0; i < messages.length; i++) {
            String senderId = (i % 2 == 0) ? user1.getUserId() : user2.getUserId();
            String receiverId = (i % 2 == 0) ? user2.getUserId() : user1.getUserId();
            
            Message message = new Message(senderId, receiverId, messages[i], project.getProjectId());
            message.setTimestamp(baseTime + (i * 60000)); // Add one minute per message
            
            storageUtil.saveMessage(message);
        }
    }
    
    /**
     * Loads an image from drawable resources and saves it to internal storage
     * @param context Application context
     * @param drawableId Resource ID of the drawable
     * @param storageUtil Storage utility instance
     * @return Path to the saved image or null if failed
     */
    private static String loadAndSaveImage(Context context, int drawableId, LocalStorageUtil storageUtil) {
        try {
            // Load bitmap from drawable resource
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
            if (bitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from resource: " + drawableId);
                return null;
            }
            
            // For better quality, ensure the bitmap is not too small
            if (bitmap.getWidth() < 300 || bitmap.getHeight() < 300) {
                Log.d(TAG, "Image is small, using original size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            }
            
            // Save bitmap to internal storage
            String imagePath = storageUtil.saveImageToStorage(bitmap);
            Log.d(TAG, "Saved image to: " + imagePath);
            return imagePath;
        } catch (Exception e) {
            Log.e(TAG, "Error loading and saving image: " + e.getMessage(), e);
            return null;
        }
    }
}