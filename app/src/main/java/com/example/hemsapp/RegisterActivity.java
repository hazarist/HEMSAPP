package com.example.hemsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUserName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSubmit;
    private TextView tvAlreadyExist;
    private FirebaseAuth mAuth;
    private ProgressDialog pdRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvAlreadyExist = findViewById(R.id.tvAlreadyExist);
        btnSubmit = findViewById(R.id.btnSubmit);

        pdRegister = new ProgressDialog(this);

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
            String userName = etUserName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                pdRegister.setTitle("Kayıt ediliyor.");
                pdRegister.setMessage("Hesabınız oluşturuluyor. Lütfen bekleyiniz..");
                pdRegister.setCanceledOnTouchOutside(false);
                pdRegister.show();
                registerUser(userName,email,password);
            }
        }

    }

    private void registerUser(String userName,String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    pdRegister.dismiss();
                    Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                    Toast.makeText(getApplicationContext(),"Hesabınız başarıyla oluşturuldu.",Toast.LENGTH_SHORT).show();
                }else
                {
                    pdRegister.dismiss();
                    Toast.makeText(getApplicationContext(),"Hata!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
