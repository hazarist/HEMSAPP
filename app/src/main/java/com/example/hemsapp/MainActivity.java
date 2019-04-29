package com.example.hemsapp;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar;
        ViewPager vPager;
        SectionPageAdapter sectionPageAdapter;
        TabLayout tabLayout;

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(mToolbar);

        if(mAuth.getCurrentUser() != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        //Tabs
        vPager = findViewById(R.id.viewPager);
        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        vPager.setAdapter(sectionPageAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(vPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivityLogin();
        }else{
            userDatabaseReference.child("online").setValue(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userDatabaseReference.child("online").setValue(false);
            userDatabaseReference.child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if(currentUser != null && currentUser.getPosition().equals(UserRole.Manager.toString())) {
                    getMenuInflater().inflate(R.menu.main_menu, menu);
                }else {
                    getMenuInflater().inflate(R.menu.main_menu2, menu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.btnLogOut){
            FirebaseAuth.getInstance().signOut();
            startActivityLogin();
        }

        if(item.getItemId() == R.id.btnSettings){
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if(item.getItemId() == R.id.btnAddTask){
            Intent newTaskIntent = new Intent(MainActivity.this,NewTaskActivity.class);
            startActivity(newTaskIntent);
        }

        if(item.getItemId() == R.id.btnAllUsers){
            Intent usersIntent = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(usersIntent);
        }

        return true;
    }

    private void startActivityLogin(){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
