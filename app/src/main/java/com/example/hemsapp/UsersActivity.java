package com.example.hemsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView rvUserList;
    private DatabaseReference usersDatabase;
    private DatabaseReference tasksDatabase;
    private String taskID;

    FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        Toolbar tbUsers;

        if(getIntent().hasExtra("taskID")) {
            taskID = getIntent().getStringExtra("taskID");
            tasksDatabase = FirebaseDatabase.getInstance().getReference().child("Tasks").child(taskID);
        }

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        tbUsers = findViewById(R.id.users_appBar);
        setSupportActionBar(tbUsers);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Users");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull final User model) {
                holder.setName(model.getFullName());
                holder.setStatus(model.getStatus());
                holder.setUserImage(model.getThumbImage());
                final String userID=getRef(position).getKey();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(taskID != null && !TextUtils.isEmpty(taskID)) {

                        CharSequence options[] = new CharSequence[]{"Assign Task", "Look Profile"};

                        final AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
                        builder.setTitle("Select Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){
                                    tasksDatabase.child("byWho").setValue(userID);
                                    Toast.makeText(UsersActivity.this,"Task assigned to " + model.getFullName()+ ".",Toast.LENGTH_SHORT).show();
                                }

                                if(which == 1) {
                                    Intent intentProfile = new Intent(UsersActivity.this, ProfileActivity.class);
                                    intentProfile.putExtra("userID", userID);
                                    startActivity(intentProfile);
                                }
                            }
                        });

                        builder.show();

                        }else {
                            Intent intentProfile = new Intent(UsersActivity.this, ProfileActivity.class);
                            intentProfile.putExtra("userID", userID);
                            startActivity(intentProfile);
                        }
                    }
                });
            }
        };

        rvUserList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
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
