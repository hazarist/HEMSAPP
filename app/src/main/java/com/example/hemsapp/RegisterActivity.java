package com.example.hemsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.sql.Date;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etSurName;
    private EditText etBirthDay;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword2;
    private Button btnSubmit;
    private TextView tvAlreadyExist;
    private FirebaseAuth mAuth;
    private ProgressDialog pdRegister;
    RadioGroup radioGroup;
    RadioButton rbGenderMale;
    RadioButton rbGenderFemale;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        //Edit Texts
        etName = findViewById(R.id.etName);
        etSurName = findViewById(R.id.etSurName);
        etBirthDay = findViewById(R.id.etBirthDay);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        //Buttons
        tvAlreadyExist = findViewById(R.id.tvAlreadyExist);
        btnSubmit = findViewById(R.id.btnSubmit);
        rbGenderMale = findViewById(R.id.rbMale);
        rbGenderFemale = findViewById(R.id.rbFemale);

        pdRegister = new ProgressDialog(this);

        //Button Listeners
        btnSubmit.setOnClickListener(this);
        tvAlreadyExist.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v.getId() == tvAlreadyExist.getId()){
            Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        if(v.getId() == btnSubmit.getId()){
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String password2 = etPassword2.getText().toString();
            String name = etName.getText().toString();
            String surName = etSurName.getText().toString();
            String status = ProfileStatus.Available.toString();
            String position = UserRole.Employee.toString();
            String image = "default";
            String thumbImage = "default";



            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(password2) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(surName)){
                if(password.equals(password2)) {
                    pdRegister.setTitle("Enrolling.");
                    pdRegister.setMessage("Creating your account.. Please wait ..");
                    pdRegister.setCanceledOnTouchOutside(false);
                    pdRegister.show();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    registerUser(name, surName, email, position, status, image, thumbImage, password, deviceToken);
                }else {
                    Toast.makeText(getApplicationContext(),"Passwords are not same!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Please fill the blanks correctly.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void registerUser(final String name, final String surName, final String email, final String position, final String status, final String image, final String thumbImage, String password, final String deviceToken){

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    String uid = currentUser.getUid();

                    firebaseDatabase = FirebaseDatabase.getInstance();

                    databaseReference = firebaseDatabase.getReference().child("Users").child(uid);

                    User newUSer = new User(name,surName,email,position,status,image,thumbImage,deviceToken);

                            databaseReference.setValue(newUSer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish();
                                    pdRegister.dismiss();
                                    Toast.makeText(getApplicationContext(),"Your account created successfully.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pdRegister.dismiss();
                                    Toast.makeText(getApplicationContext(),"Error!" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

                }else
                {
                    pdRegister.dismiss();
                    if(task.getException() != null)
                    Toast.makeText(getApplicationContext(),"Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
