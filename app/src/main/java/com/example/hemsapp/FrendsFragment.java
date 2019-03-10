package com.example.hemsapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FrendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private View mMainView;

    public FrendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_frends, container, false);

        mFriendsList = mMainView.findViewById(R.id.rvFirendList);
        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, FriendsViewHolder> firebaseRecyclerAdapter;

        Query query = mUsersDatabase
                .orderByChild("online").equalTo(true);

        FirebaseRecyclerOptions<User> options=
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query,User.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.users_single_layout, parent, false);

                    return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull User user) {

                final String userID = getRef(position).getKey();

                    holder.setName(user.getFullName());
                    holder.setStatus(user.getStatus());
                    holder.setUserImage(user.getThumbImage());
                    holder.setUserOnline(user.getOnline().toString());

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Select Options");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if(i == 0) {
                                        Intent intentProfile = new Intent(getContext(), ProfileActivity.class);
                                        intentProfile.putExtra("userID", userID);
                                        startActivity(intentProfile);
                                    }

                                    if(i == 1){
                                        Intent intentChat = new Intent(getContext(), ChatActivity.class);
                                        intentChat.putExtra("userID", userID);
                                        startActivity(intentChat);
                                    }
                                }
                            });

                            builder.show();

                        }
                    });
            }
        };

        firebaseRecyclerAdapter.startListening();
        mFriendsList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.tvUserSingleName);
            userNameView.setText(name);
        }

        public void setStatus(String status){
            TextView tvUserStatus = mView.findViewById(R.id.tvSingleStatus);
            tvUserStatus.setText(status);
        }

        public void setUserImage(String thumb_image){

            CircleImageView userImageView = mView.findViewById(R.id.ivUserSingleImage);
            Picasso.get().load(thumb_image).placeholder(R.mipmap.profile).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = mView.findViewById(R.id.ivOnlineIcon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }

}
