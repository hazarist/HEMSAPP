package com.example.hemsapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etTaskName;
    EditText etTaskDefinition;
    EditText etTaskType;
    EditText etTaskRoom;

    Button btnAddTask;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        etTaskName = findViewById(R.id.etTaskName);
        etTaskDefinition = findViewById(R.id.etDefinition);
        etTaskType = findViewById(R.id.etType);
        etTaskRoom = findViewById(R.id.etRoomNum);

        btnAddTask = findViewById(R.id.btnAddNewTask);

        btnAddTask.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks");

    }

    @Override
    public void onClick(View v) {

        final Task task = new Task(etTaskName.getText().toString(),etTaskDefinition.getText().toString(),etTaskType.getText().toString(),etTaskRoom.getText().toString());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               long counter = dataSnapshot.getChildrenCount() + 1;

               if(counter == 1){
                   databaseReference.child("1").setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {

                       }
                   });
               }else {
                   databaseReference.child(Long.toString(counter)).setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {

                       }
                   });
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        etTaskRoom.setText("");
        etTaskType.setText("");
        etTaskDefinition.setText("");
        etTaskName.setText("");
    }
}
