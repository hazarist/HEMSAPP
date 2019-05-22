package com.example.hemsapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etSurName;
    private EditText etBirthDay;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword2;
    private Button btnSubmit;
    private Button btnDataPicker;
    private TextView tvAlreadyExist;
    private FirebaseAuth mAuth;
    private ProgressDialog pdRegister;
    private RadioGroup radioGroup;
    private RadioButton rbGenderMale;
    private RadioButton rbGenderFemale;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbDatabaseUsers;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.etName);
        etSurName = findViewById(R.id.etSurName);
        etBirthDay = findViewById(R.id.etBirthDay);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        tvAlreadyExist = findViewById(R.id.tvAlreadyExist);

        radioGroup = findViewById(R.id.rgGender);
        rbGenderFemale = findViewById(R.id.rbFemale);
        rbGenderMale = findViewById(R.id.rbMale);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnDataPicker = findViewById(R.id.btnDatePicker);

        rbGenderMale = findViewById(R.id.rbMale);
        rbGenderFemale = findViewById(R.id.rbFemale);

        pdRegister = new ProgressDialog(this);

        btnSubmit.setOnClickListener(this);
        btnDataPicker.setOnClickListener(this);
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
            String birthDay = etBirthDay.getText().toString();
            String status = ProfileStatus.Available.toString();
            String position = UserRole.Employee.toString();
            String image = "default";
            String thumbImage = "default";
            String gender = "";
            int selectedGender = radioGroup.getCheckedRadioButtonId();
            switch (selectedGender){
                case R.id.rbMale:
                {
                    gender = rbGenderMale.getText().toString();
                    break;
                }
                case R.id.rbFemale:
                {
                    gender = rbGenderFemale.getText().toString();
                    break;
                }
            }

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(password2)
                    && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(surName)
                    && !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(birthDay)){
                if(password.equals(password2)) {
                    pdRegister.setTitle("Enrolling.");
                    pdRegister.setMessage("Creating your account.. Please wait ..");
                    pdRegister.setCanceledOnTouchOutside(false);
                    pdRegister.show();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    registerUser(name, surName,birthDay ,email, position, status, image, gender, thumbImage, password, deviceToken);
                }else {
                    Toast.makeText(getApplicationContext(),"Passwords are not same!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Please fill the blanks correctly.", Toast.LENGTH_SHORT).show();
            }
        }

        if(v.getId() == btnDataPicker.getId()){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+=1;
                    etBirthDay.setText(dayOfMonth+"/"+month+"/"+year);
                }
            },year,month,day);
            datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,"Select",datePickerDialog);
            datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE,"Cancel",datePickerDialog);
            datePickerDialog.show();
        }

    }

    private void registerUser(final String name, final String surName,final String birthDay, final String email, final String position, final String status,final  String gender, final String image, final String thumbImage, String password, final String deviceToken){

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (currentUser != null) {

                        String uid = currentUser.getUid();

                        firebaseDatabase = FirebaseDatabase.getInstance();

                        dbDatabaseUsers = firebaseDatabase.getReference().child("Users").child(uid);


                    User newUSer = new User(uid,name,surName,birthDay,email,position,status,gender,image,thumbImage,deviceToken);

                        dbDatabaseUsers.setValue(newUSer).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    }
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
