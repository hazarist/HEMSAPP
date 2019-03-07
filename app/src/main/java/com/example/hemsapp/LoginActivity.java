package com.example.hemsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvCreateAccount;

    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mAuth = FirebaseAuth.getInstance();
        
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvNeedAccount);

        tvCreateAccount.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == tvCreateAccount.getId()){
            Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(registerIntent);
        }
        
        if(v.getId() == btnLogin.getId()) {

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful() && mAuth != null && mAuth.getCurrentUser() != null) {
                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                    databaseReference = firebaseDatabase.getReference("Users").child(mAuth.getCurrentUser().getUid());

                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                         User currentUser = dataSnapshot.getValue(User.class);

                                         if(currentUser != null && currentUser.getPosition().equals("Employee")){
                                             Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                             startActivity(mainIntent);
                                             finish();
                                             Toast.makeText(getApplicationContext(), "Login process is successful.", Toast.LENGTH_SHORT).show();
                                         }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(), "Error! " + task.getException(), Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }else {
                Toast.makeText(getApplicationContext(),"Please enter your e-mail address and password.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
