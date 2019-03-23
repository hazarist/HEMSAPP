package com.example.hemsapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class Main2Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar mToolbar;
        ViewPager vPager;
        SectionPageAdapterManager sectionPageAdapter;
        TabLayout tabLayout;

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.appBarLayout);
        setSupportActionBar(mToolbar);

        if(mAuth.getCurrentUser() != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        //Tabs
        vPager = findViewById(R.id.viewPager);
        sectionPageAdapter = new SectionPageAdapterManager(getSupportFragmentManager());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

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
            Intent settingsIntent = new Intent(Main2Activity.this,SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if(item.getItemId() == R.id.btnAddTask){
            Intent newTaskIntent = new Intent(Main2Activity.this,NewTaskActivity.class);
            startActivity(newTaskIntent);
        }

        if(item.getItemId() == R.id.btnAllUsers){
            Intent usersIntent = new Intent(Main2Activity.this,UsersActivity.class);
            startActivity(usersIntent);
        }

        return true;
    }

    private void startActivityLogin(){
        Intent loginIntent = new Intent(Main2Activity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
