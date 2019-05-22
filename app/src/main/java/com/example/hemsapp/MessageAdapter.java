package com.example.hemsapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {



    private List<Messages> mMessageList;
    private DatabaseReference dbReferenceUser;

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

        private TextView messageText;
        private CircleImageView profileImage;
        private TextView displayName;
        private ImageView messageImage;
        private TextView tvTime;

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

        final Messages c = mMessageList.get(i);

        final String fromUser = c.getFrom();
        final String message_type = c.getType();

        dbReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUser);

        dbReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                long lastTime =  user.getLastSeen();


                if(user != null) {
                    viewHolder.displayName.setText(user.getFullName());
                    Picasso.get().load(user.getThumbImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.profile).into(viewHolder.profileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(user.getThumbImage()).placeholder(R.mipmap.profile).into(viewHolder.profileImage);
                        }
                    });
                }

                Date d = new Date(c.getTime());
                Calendar calendar = new GregorianCalendar().getInstance();
                calendar.setTime(d);
                viewHolder.tvTime.setText(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + Integer.toString(calendar.get(Calendar.MINUTE)));

                if(message_type.equals("text")) {
                    viewHolder.messageText.setText(c.getMessage());
                    viewHolder.messageImage.setVisibility(View.INVISIBLE);

                } else {
                    viewHolder.messageText.setVisibility(View.INVISIBLE);
                    Picasso.get().load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.messageImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(c.getMessage()).into(viewHolder.messageImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}


