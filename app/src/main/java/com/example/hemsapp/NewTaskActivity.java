package com.example.hemsapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvAssignedEmployee;

    private EditText etTaskName;
    private EditText etTaskDescription;
    private EditText etTaskLocation;

    private RadioGroup radioGroup;
    private RadioButton rbUrgent;
    private RadioButton rbImportant;
    private RadioButton rbStandard;

    private Button btnAddTask;
    private Button btnChangeAssignedEmployee;

    private Spinner spnTaskType;
    private Spinner spnTaskSubtype;

    private DatabaseReference dbReferenceTasks;
    private DatabaseReference dbReferenceUSers;

    private long taskID  = 0;

    private String selectedTaskType;
    private String selectedTaskSubtype;
    private String currentTaskID;
    private Task task;
    private String userUid;
    private int counterUser = 0;
    private int counterIns = 0;
    private Instance[] instances;
    private String[] allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        tvAssignedEmployee = findViewById(R.id.tvAssignedEmployee);

        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskLocation = findViewById(R.id.etTaskLocation);

        radioGroup = findViewById(R.id.rgTaskPriority);
        rbUrgent = findViewById(R.id.rbUrgent);
        rbImportant = findViewById(R.id.rbImportant);
        rbStandard = findViewById(R.id.rbStandard);
        rbStandard.setChecked(true);

        spnTaskType = findViewById(R.id.spnTaskType);
        spnTaskType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTaskType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTaskType = parent.getItemAtPosition(0).toString();
            }
        });

        spnTaskSubtype = findViewById(R.id.spnTaskSubtype);
        spnTaskSubtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTaskSubtype = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTaskSubtype = parent.getItemAtPosition(0).toString();
            }
        });

        btnAddTask = findViewById(R.id.btnAddNewTask);
        btnChangeAssignedEmployee = findViewById(R.id.btnChangeAssignedEmployee);

        btnAddTask.setOnClickListener(this);
        btnChangeAssignedEmployee.setOnClickListener(this);

        dbReferenceTasks = FirebaseDatabase.getInstance().getReference().child("Tasks");
        dbReferenceUSers = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbReferenceUSers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUsers = new String[(int)dataSnapshot.getChildrenCount()];
                counterUser = 0;
                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()){
                    User user = userDataSnapshot.getValue(User.class);
                    if( !user.getStatus().equals("Busy") && !user.getPosition().equals("Manager") ) {
                        allUsers[counterUser] = user.getUid();
                        counterUser++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(getIntent().hasExtra("taskID")) {
            currentTaskID = getIntent().getStringExtra("taskID");

            if(getIntent().hasExtra("userID")) {
                userUid = getIntent().getStringExtra("userID");
                if(!userUid.equals("It is in queue.")) {

                    dbReferenceUSers.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
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


            dbReferenceTasks.child(currentTaskID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task = dataSnapshot.getValue(Task.class);
                    if(task != null) {
                        etTaskName.setText(task.getName());
                        etTaskDescription.setText(task.getDescription());
                        if(task.getType().equals("Room")){
                            spnTaskType.setSelection(0);
                        }else if(task.getType().equals("Garden")){
                            spnTaskType.setSelection(1);
                        }else if(task.getType().equals("Kitchen")){
                            spnTaskType.setSelection(2);
                        }

                        if(task.getSubType().equals("Service")){
                            spnTaskSubtype.setSelection(0);
                        }else if(task.getSubType().equals("Cleaning")){
                            spnTaskSubtype.setSelection(1);
                        }

                        etTaskLocation.setText(task.getLocation());
                        if(task.getPriority().equals("Urgent")){
                            rbUrgent.setChecked(true);
                        } else if (task.getPriority().equals("Important")) {
                            rbImportant.setChecked(true);
                        }else if(task.getPriority().equals("Standard")){
                            rbStandard.setChecked(true);
                        }
                        tvAssignedEmployee.setText("Update Task");
                        btnAddTask.setText("UPDATE TASK");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            etTaskLocation.setText("");
            etTaskDescription.setText("");
            spnTaskType.setSelection(0);
            spnTaskSubtype.setSelection(0);
            etTaskName.setText("");
            rbStandard.setChecked(true);
            tvAssignedEmployee.setText("New Task");
            btnAddTask.setText("ADD TASK");
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == btnAddTask.getId()) {
            String taskName = etTaskName.getText().toString();
            String taskDescription = etTaskDescription.getText().toString();
            String taskType = selectedTaskType;
            String taskSubtpye = selectedTaskSubtype;
            String taskLocation = etTaskLocation.getText().toString();
            final int checkedItem = radioGroup.getCheckedRadioButtonId();
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
                case R.id.rbStandard: {
                    taskPriority = rbStandard.getText().toString();
                    break;
                }
            }

            if (!TextUtils.isEmpty(taskName) && !TextUtils.isEmpty(taskDescription) && !TextUtils.isEmpty(taskType) && !TextUtils.isEmpty(taskSubtpye) && !TextUtils.isEmpty(taskLocation) && !TextUtils.isEmpty(taskPriority)) {

                if (currentTaskID == null) {
                    task = new Task(taskName, taskDescription, taskType, taskSubtpye, taskLocation, taskPriority,"0");
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
                    if (!task.getSubType().equals(taskSubtpye))
                        task.setSubType(taskSubtpye);
                }

                dbReferenceTasks.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        instances = new Instance[(int) dataSnapshot.getChildrenCount()];
                        counterIns = 0;
                        for (DataSnapshot insDataSanpshot : dataSnapshot.getChildren()){
                            Instance ins = insDataSanpshot.getValue(Instance.class);
                            if(ins.getEvaluation() != null && ins.getSubtype().equals(task.getSubType())) {
                                instances[counterIns] = ins;
                                counterIns++;
                            }
                        }

                        NaiveBayes nv = new NaiveBayes();
                        final String predictedEmployee = nv.NaiveBayes(instances,counterIns,allUsers,counterUser,task);

                        if (currentTaskID == null) {
                            taskID = dataSnapshot.getChildrenCount() + 1;
                        } else {
                            taskID = Integer.parseInt(currentTaskID);
                            currentTaskID = null;
                        }

                        dbReferenceTasks.child(Long.toString(taskID)).setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dbReferenceTasks.child(Long.toString(taskID)).child("taskCreateTime").setValue(ServerValue.TIMESTAMP);
                                dbReferenceTasks.child(Long.toString(taskID)).child("predictedEmployee").setValue(predictedEmployee);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(NewTaskActivity.this, "New task successfully added.", Toast.LENGTH_SHORT).show();
                etTaskLocation.setText("");
                etTaskDescription.setText("");
                spnTaskType.setSelection(0);
                spnTaskSubtype.setSelection(0);
                etTaskName.setText("");
                rbStandard.setChecked(true);
                tvAssignedEmployee.setText("New Task");
                btnAddTask.setText("ADD TASK");
            } else {
                Toast.makeText(NewTaskActivity.this, "Please fill the blanks!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
