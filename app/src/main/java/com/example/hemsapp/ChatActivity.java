package com.example.hemsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar chatToolbar;
        final DatabaseReference rootRef;
        String chatUser = getIntent().getStringExtra("userID");

        chatToolbar = findViewById(R.id.chatAppBar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();


        rootRef = FirebaseDatabase.getInstance().getReference();



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


        rootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if(user != null) {
                    tvFullName.setText(user.getFullName());

                    Picasso.get().load(user.getThumbImage()).placeholder(R.mipmap.profile).into(ivChatUserImage);

                    if (user.getOnline()) {
                        tvLastSeen.setText("online");
                    } else {
                        tvLastSeen.setText("Last seen: " + user.getLastSeen());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
