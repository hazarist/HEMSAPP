package com.example.hemsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private String currentUserID;

    private ImageButton btnChatAdd;
    private ImageButton btnChatSend;
    private EditText etChatMessage;
    String chatUser;
    DatabaseReference rootRef;

    private RecyclerView mMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private DatabaseReference messageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar chatToolbar;
        chatUser = getIntent().getStringExtra("userID");

        chatToolbar = findViewById(R.id.chatAppBar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();


        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null && mAuth.getCurrentUser() != null)
        currentUserID = mAuth.getCurrentUser().getUid();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_custom_bar, null);

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionBarView);
        }

        final TextView tvFullName = findViewById(R.id.tvChatUserName);
        final TextView tvLastSeen = findViewById(R.id.tvChatLastSeen);
        final CircleImageView ivChatUserImage = findViewById(R.id.imChatUserImage);
        btnChatAdd = findViewById(R.id.btnChatAdd);
        btnChatSend = findViewById(R.id.btnChatSend);
        etChatMessage = findViewById(R.id.etChatMessage);

        messageAdapter = new MessageAdapter(messagesList);

        mMessagesList = findViewById(R.id.messageList);
        linearLayoutManager = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(linearLayoutManager);
        mMessagesList.setAdapter(messageAdapter);

        loadMessages();

        btnChatSend.setOnClickListener(this);

        rootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if(user != null) {
                    tvFullName.setText(user.getFullName());

                    Picasso.get().load(user.getThumbImage()).placeholder(R.mipmap.profile).into(ivChatUserImage);

                    if (user.getOnline()) {
                        tvLastSeen.setText(R.string.rs_online);
                    } else {

                        GetTimeAgo getTimeAgo = new GetTimeAgo();

                        long lastTime =  user.getLastSeen();

                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());

                        tvLastSeen.setText(lastSeenTime);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        rootRef.child("Chat").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(chatUser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatAddMap.put("Chat/"+currentUserID+"/"+chatUser,chatAddMap);
                    chatAddMap.put("Chat/"+chatUser+"/"+currentUserID,chatAddMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){

                                Log.d("Chat_LOG",databaseError.getMessage());

                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {
        rootRef.child("messages").child(currentUserID).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnChatSend.getId()){

            sendMessage();

        }
    }

    private void sendMessage(){
        String message = etChatMessage.getText().toString();

        if(!TextUtils.isEmpty(message)){
            String currentUserRef = "messages/" + currentUserID + "/" + chatUser;
            String chatUserRef = "messages/" + chatUser + "/" + currentUserID;

            DatabaseReference userMessagePush = rootRef.child("messages").child(currentUserID).child(chatUser).push();

            String pushID = userMessagePush.getKey();

            Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", currentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef +"/"+pushID,messageMap);
            messageUserMap.put(chatUserRef+"/"+pushID,messageMap);

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null){

                    }
                }
            });

        }
        etChatMessage.setText("");
    }
}
