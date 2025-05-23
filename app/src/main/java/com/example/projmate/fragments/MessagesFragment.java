package com.example.projmate.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projmate.R;
import com.example.projmate.chat.ChatActivity;
import com.example.projmate.model.Message;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessagesFragment extends Fragment {

    private RecyclerView rvConversations;
    private TextView tvNoMessages;
    
    private LocalStorageUtil storageUtil;
    private User currentUser;
    private ConversationAdapter conversationAdapter;
    private List<Conversation> conversations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        rvConversations = view.findViewById(R.id.rvConversations);
        tvNoMessages = view.findViewById(R.id.tvNoMessages);
        
        // Initialize storage and get current user
        storageUtil = LocalStorageUtil.getInstance(requireContext());
        currentUser = storageUtil.getCurrentUser();
        
        // Set up RecyclerView
        rvConversations.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Load conversations
        loadConversations();
    }
    
    private void loadConversations() {
        if (currentUser == null) return;
        
        // Get all messages for current user
        List<Message> userMessages = storageUtil.getAllMessagesForUser(currentUser.getUserId());
        
        if (userMessages.isEmpty()) {
            rvConversations.setVisibility(View.GONE);
            tvNoMessages.setVisibility(View.VISIBLE);
            return;
        }
        
        // Group messages by conversation (project + other user)
        Map<String, Conversation> conversationMap = new HashMap<>();
        
        for (Message message : userMessages) {
            String otherUserId = message.getSenderId().equals(currentUser.getUserId()) 
                    ? message.getReceiverId() : message.getSenderId();
            
            String projectId = message.getProjectId();
            String conversationKey = projectId + "_" + otherUserId;
            
            if (!conversationMap.containsKey(conversationKey)) {
                User otherUser = storageUtil.getUserById(otherUserId);
                Project project = storageUtil.getProjectById(projectId);
                
                if (otherUser != null && project != null) {
                    Conversation conversation = new Conversation();
                    conversation.otherUser = otherUser;
                    conversation.project = project;
                    conversation.lastMessage = message;
                    conversationMap.put(conversationKey, conversation);
                }
            } else {
                Conversation existing = conversationMap.get(conversationKey);
                if (existing.lastMessage.getTimestamp() < message.getTimestamp()) {
                    existing.lastMessage = message;
                }
            }
        }
        
        // Convert map to list and sort by timestamp (newest first)
        conversations = new ArrayList<>(conversationMap.values());
        Collections.sort(conversations, (c1, c2) -> 
                Long.compare(c2.lastMessage.getTimestamp(), c1.lastMessage.getTimestamp()));
        
        // Update UI
        if (conversations.isEmpty()) {
            rvConversations.setVisibility(View.GONE);
            tvNoMessages.setVisibility(View.VISIBLE);
        } else {
            rvConversations.setVisibility(View.VISIBLE);
            tvNoMessages.setVisibility(View.GONE);
            
            conversationAdapter = new ConversationAdapter(conversations);
            rvConversations.setAdapter(conversationAdapter);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload conversations in case they were updated
        loadConversations();
    }
    
    // Conversation data class
    private static class Conversation {
        User otherUser;
        Project project;
        Message lastMessage;
    }
    
    // Conversation adapter for RecyclerView
    private class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
        
        private List<Conversation> conversations;
        private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        
        public ConversationAdapter(List<Conversation> conversations) {
            this.conversations = conversations;
        }
        
        @NonNull
        @Override
        public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
            return new ConversationViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
            Conversation conversation = conversations.get(position);
            holder.bind(conversation);
        }
        
        @Override
        public int getItemCount() {
            return conversations.size();
        }
        
        class ConversationViewHolder extends RecyclerView.ViewHolder {
            
            private ImageView ivProjectImage;
            private TextView tvProjectName, tvUserName, tvLastMessage, tvTimestamp;
            
            public ConversationViewHolder(@NonNull View itemView) {
                super(itemView);
                
                ivProjectImage = itemView.findViewById(R.id.ivProjectImage);
                tvProjectName = itemView.findViewById(R.id.tvProjectName);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
                tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            }
            
            public void bind(Conversation conversation) {
                tvProjectName.setText(conversation.project.getName());
                tvUserName.setText(conversation.otherUser.getName());
                tvLastMessage.setText(conversation.lastMessage.getContent());
                tvTimestamp.setText(timeFormat.format(new Date(conversation.lastMessage.getTimestamp())));
                
                // Load project image if available
                if (conversation.project.getImageUri() != null && !conversation.project.getImageUri().isEmpty()) {
                    try {
                        Bitmap bitmap = storageUtil.loadImageFromStorage(conversation.project.getImageUri());
                        conversation.project.setImageBitmap(bitmap);
                        if (conversation.project.getImageBitmap() != null) {
                            ivProjectImage.setImageBitmap(conversation.project.getImageBitmap());
                        } else {
                            ivProjectImage.setImageResource(R.drawable.placeholder_project);
                        }
                    } catch (OutOfMemoryError oom) {
                        Log.e("MessagesFragment", "Out of memory error loading image: " + oom.getMessage());
                        ivProjectImage.setImageResource(R.drawable.placeholder_project);
                        System.gc();
                    }
                } else {
                    ivProjectImage.setImageResource(R.drawable.placeholder_project);
                }
                
                // Set click listener to open chat
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext(), ChatActivity.class);
                    intent.putExtra("PROJECT_ID", conversation.project.getProjectId());
                    intent.putExtra("RECEIVER_ID", conversation.otherUser.getUserId());
                    startActivity(intent);
                });
            }
        }
    }
}