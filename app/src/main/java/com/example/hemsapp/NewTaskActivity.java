package com.example.hemsapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvAssignedEmployee;

    EditText etTaskName;
    EditText etTaskDescription;
    EditText etTaskType;
    EditText etTaskLocation;

    RadioGroup radioGroup;
    RadioButton rbUrgent;
    RadioButton rbImportant;
    RadioButton rbStandard;

    Button btnAddTask;
    Button btnChangeAssignedEmployee;

    DatabaseReference databaseReference;
    DatabaseReference userDatabaseReference;

    private long taskID  = 0;

    private  String currentTaskID;
    private Task task;
    private String userUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        tvAssignedEmployee = findViewById(R.id.tvAssignedEmployee);

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
        btnChangeAssignedEmployee = findViewById(R.id.btnChangeAssignedEmployee);

        btnAddTask.setOnClickListener(this);
        btnChangeAssignedEmployee.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(getIntent().hasExtra("taskID")) {
            currentTaskID = getIntent().getStringExtra("taskID");

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks");

            if(getIntent().hasExtra("userID")) {
                userUid = getIntent().getStringExtra("userID");
                if(!userUid.equals("It is in queue.")) {
                    userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    userDatabaseReference.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            tvAssignedEmployee.setTextColor(Color.BLACK);
                            tvAssignedEmployee.setText(user.getFullName());
                            tvAssignedEmployee.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intentUsers = new Intent(NewTaskActivity.this,UsersActivity.class);
                                    intentUsers.putExtra("taskID",currentTaskID);
                                    startActivity(intentUsers);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }


            databaseReference.child(currentTaskID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task = dataSnapshot.getValue(Task.class);
                    if(task != null) {
                        etTaskName.setText(task.getName());
                        etTaskDescription.setText(task.getDescription());
                        etTaskType.setText(task.getType());
                        etTaskLocation.setText(task.getLocation());
                        if(task.getPriority().equals("Urgent")){
                            rbUrgent.setChecked(true);
                        } else if (task.getPriority().equals("Important")) {
                            rbImportant.setChecked(true);
                        }else if(task.getPriority().equals("Standard")){
                            rbStandard.setChecked(true);
                        }
                        btnAddTask.setText("UPDATE TASK");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            etTaskLocation.setText("");
            etTaskType.setText("");
            etTaskDescription.setText("");
            etTaskName.setText("");
            rbStandard.setChecked(true);
            btnAddTask.setText("ADD TASK");
        }

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == btnAddTask.getId()) {
            String taskName = etTaskName.getText().toString();
            String taskDescription = etTaskDescription.getText().toString();
            String taskType = etTaskType.getText().toString();
            String taskLocation = etTaskLocation.getText().toString();
            int checkedItem = radioGroup.getCheckedRadioButtonId();
            String taskPriority = "";
            switch (checkedItem) {
                case R.id.rbUrgent: {
                    taskPriority = rbUrgent.getText().toString();
                    break;
                }
                case R.id.rbImportant: {
                    taskPriority = rbImportant.getText().toString();
                    break;
                }
                case R.id.rbStandart: {
                    taskPriority = rbStandard.getText().toString();
                    break;
                }
            }

            if (!TextUtils.isEmpty(taskName) && !TextUtils.isEmpty(taskDescription) && !TextUtils.isEmpty(taskType) && !TextUtils.isEmpty(taskLocation) && !TextUtils.isEmpty(taskPriority)) {

                if (currentTaskID == null) {
                    task = new Task(taskName, taskDescription, taskType, taskLocation, taskPriority,"0");
                } else {
                    if (!task.getName().equals(taskName))
                        task.setName(taskName);
                    if (!task.getDescription().equals(taskDescription))
                        task.setDescription(taskDescription);
                    if (!task.getType().equals(taskType))
                        task.setType(taskType);
                    if (!task.getLocation().equals(taskLocation))
                        task.setLocation(taskLocation);
                    if (!task.getPriority().equals(taskPriority))
                        task.setPriority(taskPriority);
                }

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if (currentTaskID == null) {
                            taskID = dataSnapshot.getChildrenCount() + 1;
                        } else {
                            taskID = Integer.parseInt(currentTaskID);
                            currentTaskID = null;
                        }

                        databaseReference.child(Long.toString(taskID)).setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(Long.toString(taskID)).child("taskCreateTime").setValue(ServerValue.TIMESTAMP);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(NewTaskActivity.this, "New task successfully added.", Toast.LENGTH_SHORT).show();
                etTaskLocation.setText("");
                etTaskType.setText("");
                etTaskDescription.setText("");
                etTaskName.setText("");
                rbStandard.setChecked(true);
                btnAddTask.setText("ADD TASK");
            } else {
                Toast.makeText(NewTaskActivity.this, "Please fill the blanks!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
