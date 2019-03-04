package com.example.hemsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private StorageReference mStorageRef;
    private DatabaseReference currentUserData;
    private FirebaseUser currentUser;
    private TextView tvUserName;
    private TextView tvStatus;
    private Button btnChangeImage;
    private ImageView ivProfile;

    private static final int GALLERY_PICK = 1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvUserName = findViewById(R.id.tvUserName);
        tvStatus = findViewById(R.id.tvStatus);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        ivProfile = findViewById(R.id.ivProfile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        currentUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        currentUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);

                tvUserName.setText(currentUser.getName() + " " + currentUser.getSurname());
                tvStatus.setText(currentUser.getStatus());

                Picasso.get().load(currentUser.getImage()).into(ivProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();

        btnChangeImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnChangeImage.getId()){

            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"), GALLERY_PICK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
                Uri imageUri = data.getData();

                CropImage.activity(imageUri).setAspectRatio(1,1).start(this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    progressDialog = new ProgressDialog(SettingsActivity.this);
                    progressDialog.setTitle("Uploading image..");
                    progressDialog.setMessage("Please wait while we upload and process the image.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Uri resultUri = result.getUri();

                    String currentUserUid = currentUser.getUid();

                    final StorageReference filePath = mStorageRef.child("profile_images").child(currentUserUid + ".jpg");
                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    currentUserData.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Your profile photo is changed successfully.",Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    });
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getApplicationContext(),"Error! " + error,Toast.LENGTH_SHORT).show();
                }
            }

    }
}
