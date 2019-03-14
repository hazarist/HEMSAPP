package com.example.hemsapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;


    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        CircleImageView profileImage;
        TextView displayName;
        ImageView messageImage;
        TextView tvTime;

        public MessageViewHolder(View view) {
            super(view);

            messageText =  view.findViewById(R.id.tvMessageText);
            profileImage =  view.findViewById(R.id.messageProfileIcon);
            displayName =  view.findViewById(R.id.name_text_layout);
            messageImage =  view.findViewById(R.id.message_image_layout);
            tvTime = view.findViewById(R.id.time_text_layout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();

        Messages c = mMessageList.get(i);

        String fromUser = c.getFrom();
        String message_type = c.getType();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUser);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

//                GetTimeAgo timeAgo = new GetTimeAgo();
//                long lastTime =  user.getLastSeen();
//                String lastSeenTime = timeAgo.getTimeAgo(lastTime,getApplicationContext());

                if(user != null&& mAuth != null && mAuth.getCurrentUser() != null) {
                    viewHolder.displayName.setText(user.getFullName());

                        Picasso.get().load(user.getThumbImage())
                                .placeholder(R.mipmap.profile).into(viewHolder.profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {
            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.mipmap.app_logo).into(viewHolder.messageImage);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}


