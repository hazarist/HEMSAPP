package com.example.hemsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference currentUserData;
    private FirebaseUser currentUser;
    private TextView tvUserName;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvUserName = findViewById(R.id.tvUserName);
        tvStatus = findViewById(R.id.tvStatus);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        currentUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        currentUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);

                tvUserName.setText(currentUser.getName() + " " + currentUser.getSurname());
                tvStatus.setText(currentUser.getStatus());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
