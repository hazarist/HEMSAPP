package com.example.hemsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar tbUsers;

    private RecyclerView rvUserList;

    private DatabaseReference usersDatabase;

    private  FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        tbUsers = findViewById(R.id.users_appBar);
        setSupportActionBar(tbUsers);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        rvUserList = findViewById(R.id.rvUserList);
        rvUserList.setHasFixedSize(true);
        rvUserList.setLayoutManager(new LinearLayoutManager(UsersActivity.this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options=
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(usersDatabase,User.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder( UsersViewHolder holder, int position, User model) {
                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setUserImage(model.getThumbImage());

                final String userID=getRef(position).getKey();
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentProfile=new Intent(UsersActivity.this,ProfileActivity.class);
                        intentProfile.putExtra("userID",userID);
                        startActivity(intentProfile);
                    }
                });
            }
        };

        firebaseRecyclerAdapter.startListening();
        rvUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View view;

        public UsersViewHolder(View itemView) {
            super(itemView);

            view = itemView;

        }

        public void setName(String name){
            TextView tvUserName = view.findViewById(R.id.tvUserSingleName);
            tvUserName.setText(name);
        }

        public void setStatus(String status){
            TextView tvUserStatus = view.findViewById(R.id.tvSingleStatus);
            tvUserStatus.setText(status);
        }

        public void setUserImage(String thumbImage){
            CircleImageView userImageView = view.findViewById(R.id.ivUserSingleImage);
            Picasso.get().load(thumbImage).placeholder(R.mipmap.profile).into(userImageView);
        }

    }


}
