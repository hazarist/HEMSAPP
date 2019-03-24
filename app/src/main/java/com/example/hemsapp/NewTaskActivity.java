package com.example.hemsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etTaskName;
    EditText etTaskDescription;
    EditText etTaskType;
    EditText etTaskLocation;

    RadioGroup radioGroup;
    RadioButton rbUrgent;
    RadioButton rbImportant;
    RadioButton rbStandard;

    Button btnAddTask;

    DatabaseReference databaseReference;

    long counter  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskType = findViewById(R.id.etTaskType);
        etTaskLocation = findViewById(R.id.etTaskLocation);

        radioGroup = findViewById(R.id.rgTaskPriority);
        rbUrgent = findViewById(R.id.rbUrgent);
        rbImportant = findViewById(R.id.rbImportant);
        rbStandard = findViewById(R.id.rbStandart);
        rbStandard.setChecked(true);

        btnAddTask = findViewById(R.id.btnAddNewTask);

        btnAddTask.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks");

    }

    @Override
    public void onClick(View v) {

        String taskName = etTaskName.getText().toString();
        String taskDescription = etTaskDescription.getText().toString();
        String taskType = etTaskType.getText().toString();
        String taskLocation = etTaskLocation.getText().toString();
        int checkedItem = radioGroup.getCheckedRadioButtonId();
        String taskPriority = "";
        switch (checkedItem){
            case R.id.rbUrgent:
            {
                taskPriority = rbUrgent.getText().toString();
                break;
            }
            case R.id.rbImportant:
            {
                taskPriority = rbImportant.getText().toString();
                break;
            }
            case R.id.rbStandart: {
                taskPriority = rbStandard.getText().toString();
                break;
            }
        }

        if(!TextUtils.isEmpty(taskName) && !TextUtils.isEmpty(taskDescription) && !TextUtils.isEmpty(taskType) && !TextUtils.isEmpty(taskLocation) && !TextUtils.isEmpty(taskPriority))
        {

            final Task task = new Task(taskName, taskDescription, taskType, taskLocation, taskPriority);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    counter = dataSnapshot.getChildrenCount() + 1;

                    databaseReference.child(Long.toString(counter)).setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child(Long.toString(counter)).child("taskCreateTime").setValue(ServerValue.TIMESTAMP);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Toast.makeText(NewTaskActivity.this,"New task successfully added.",Toast.LENGTH_SHORT).show();
            etTaskLocation.setText("");
            etTaskType.setText("");
            etTaskDescription.setText("");
            etTaskName.setText("");
            rbStandard.setChecked(true);

        }else {
            Toast.makeText(NewTaskActivity.this,"Please fill the blanks!",Toast.LENGTH_SHORT).show();
        }
    }
}
