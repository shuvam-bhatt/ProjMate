package com.example.projmate.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projmate.R;
import com.example.projmate.model.Message;
import com.example.projmate.model.Project;
import com.example.projmate.model.User;
import com.example.projmate.util.LocalStorageUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvChatTitle, tvProjectName, tvNoMessages;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private FloatingActionButton fabSend;
    
    private LocalStorageUtil storageUtil;
    private User currentUser;
    private User receiverUser;
    private Project project;
    private String projectId;
    private String receiverId;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // Get project ID and receiver ID from intent
        projectId = getIntent().getStringExtra("PROJECT_ID");
        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        
        if (projectId == null || receiverId == null) {
            finish();
            return;
        }
        
        // Initialize storage and get users and project
        storageUtil = LocalStorageUtil.getInstance(this);
        currentUser = storageUtil.getCurrentUser();
        receiverUser = storageUtil.getUserById(receiverId);
        project = storageUtil.getProjectById(projectId);
        
        if (currentUser == null || receiverUser == null || project == null) {
            finish();
            return;
        }
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        tvChatTitle = findViewById(R.id.tvChatTitle);
        tvProjectName = findViewById(R.id.tvProjectName);
        tvNoMessages = findViewById(R.id.tvNoMessages);
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        fabSend = findViewById(R.id.fabSend);
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        // Set chat title and project name
        tvChatTitle.setText(receiverUser.getName());
        tvProjectName.setText(project.getName());
        
        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        
        // Load messages
        loadMessages();
        
        // Set click listener for send button
        fabSend.setOnClickListener(v -> sendMessage());
    }
    
    private void loadMessages() {
        List<Message> messages = storageUtil.getMessagesBetweenUsers(
                currentUser.getUserId(), receiverUser.getUserId(), projectId);
        
        if (messages.isEmpty()) {
            rvMessages.setVisibility(View.GONE);
            tvNoMessages.setVisibility(View.VISIBLE);
        } else {
            rvMessages.setVisibility(View.VISIBLE);
            tvNoMessages.setVisibility(View.GONE);
            
            messageAdapter = new MessageAdapter(messages);
            rvMessages.setAdapter(messageAdapter);
            rvMessages.scrollToPosition(messages.size() - 1);
        }
    }
    
    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        
        if (TextUtils.isEmpty(content)) {
            return;
        }
        
        // Create and save message
        Message message = new Message(
                currentUser.getUserId(),
                receiverUser.getUserId(),
                content,
                projectId
        );
        storageUtil.saveMessage(message);
        
        // Clear input field
        etMessage.setText("");
        
        // Reload messages
        loadMessages();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // Message adapter for RecyclerView
    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        
        private List<Message> messages;
        private SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        
        public MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }
        
        @Override
        public int getItemViewType(int position) {
            Message message = messages.get(position);
            return message.getSenderId().equals(currentUser.getUserId()) ? 1 : 0;
        }
        
        @Override
        public MessageViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.bind(message, getItemViewType(position));
        }
        
        @Override
        public int getItemCount() {
            return messages.size();
        }
        
        class MessageViewHolder extends RecyclerView.ViewHolder {
            
            private CardView cardMessage;
            private TextView tvMessageContent, tvMessageTime;
            
            public MessageViewHolder(View itemView) {
                super(itemView);
                
                cardMessage = itemView.findViewById(R.id.cardMessage);
                tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
                tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            }
            
            public void bind(Message message, int viewType) {
                tvMessageContent.setText(message.getContent());
                tvMessageTime.setText(timeFormat.format(new Date(message.getTimestamp())));
                
                // Set message alignment and background based on sender
                if (viewType == 1) {
                    // Current user's message (right-aligned)
                    cardMessage.setCardBackgroundColor(getResources().getColor(R.color.purple_light));
                    ((android.view.ViewGroup.MarginLayoutParams) cardMessage.getLayoutParams()).setMarginStart(
                            (int) getResources().getDimension(R.dimen.message_margin_large));
                    ((android.view.ViewGroup.MarginLayoutParams) cardMessage.getLayoutParams()).setMarginEnd(
                            (int) getResources().getDimension(R.dimen.message_margin_small));
                    cardMessage.setLayoutParams(cardMessage.getLayoutParams());
                    cardMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    // Receiver's message (left-aligned)
                    cardMessage.setCardBackgroundColor(getResources().getColor(R.color.gray_medium));
                    ((android.view.ViewGroup.MarginLayoutParams) cardMessage.getLayoutParams()).setMarginStart(
                            (int) getResources().getDimension(R.dimen.message_margin_small));
                    ((android.view.ViewGroup.MarginLayoutParams) cardMessage.getLayoutParams()).setMarginEnd(
                            (int) getResources().getDimension(R.dimen.message_margin_large));
                    cardMessage.setLayoutParams(cardMessage.getLayoutParams());
                    cardMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }
            }
        }
    }
}