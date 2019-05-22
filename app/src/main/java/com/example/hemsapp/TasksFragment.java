package com.example.hemsapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncRequest;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment {

    private View mMainView;
    private RecyclerView rvTaskList;
    private DatabaseReference dbReferenceTasks;
    private DatabaseReference dbReferenceTaskAverage;
    private DatabaseReference dbReferenceUser;
    private FirebaseRecyclerAdapter<Task,TaskViewHolder> firebaseRecyclerAdapter;
    private FirebaseAuth mAuth;
    private static User currentUser ;
    private User predictedUser;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




        mMainView = inflater.inflate(R.layout.fragment_tasks, container, false);

        rvTaskList = mMainView.findViewById(R.id.task_list);

        dbReferenceTasks = FirebaseDatabase.getInstance().getReference().child("Tasks");
        dbReferenceTasks.keepSynced(true);

        dbReferenceTaskAverage = FirebaseDatabase.getInstance().getReference().child("TasksAverage");

        dbReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        dbReferenceUser.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rvTaskList.setHasFixedSize(true);
        rvTaskList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //TODO: order by their priority
        Query query = dbReferenceTasks.orderByChild("isDeleted").equalTo("0");

        FirebaseRecyclerOptions<Task> options =
                new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(query,Task.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull final Task model) {
                holder.setTasksName(model.getName());
                holder.setTasksPriority(model.getPriority());
                holder.setTasksState(model.getState());
                holder.setTasksDidByWho(model.getByWho());
                final String taskID = getRef(position).getKey();

                holder.mview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                            if(currentUser != null && currentUser.getPosition().equals(UserRole.Manager.toString())) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Delete!");
                                builder.setMessage("Do you want to delete this task?");
                                builder.setPositiveButton("Cancel",null);

                                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbReferenceTasks.child(taskID).child("isDeleted").setValue("1");
                                    }
                                });
                                builder.show();
                            }

                            if(currentUser != null && currentUser.getPosition().equals(UserRole.Employee.toString())){
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                if(model.getByWho().equals("It is in queue.")) {

                                    builder.setTitle("Take a task.");
                                    builder.setMessage(
                                              "Task Name: " + model.getName() + "\n"
                                            + "Task Type: " + model.getType() + "\n"
                                            + "Task Subtype: "+ model.getSubType() + "\n"
                                            + "Task Description: " + model.getDescription() + "\n"
                                            + "Task Location: " + model.getLocation() + "\n"
                                            + "Task Priority: " + model.getPriority()  + "\n"
                                            +"\nDo you want to take this task?");

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(), "Task assigned operation canceled.", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (model.getByWho().equals("It is in queue.")) {
                                                dbReferenceTasks.child(taskID).child("byWho").setValue(currentUser.getUid());
                                                dbReferenceTasks.child(taskID).child("taskAssignTime").setValue(ServerValue.TIMESTAMP);
                                                dbReferenceUser.child(currentUser.getUid()).child("status").setValue("Busy");
                                                Toast.makeText(getContext(), "Task assigned to you.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (model.getByWho().equals(currentUser.getUid())) {
                                                    Toast.makeText(getContext(), "Task already assigned to you.", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(getContext(), "Task already assigned to an employee.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    builder.show();
                                }

                                if(currentUser != null && model.getByWho().equals(currentUser.getUid()) && model.getTaskDoneTime() == 0) {
                                    builder.setTitle("Task");
                                    builder.setMessage("Is it done?");

                                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dbReferenceTasks.child(taskID).child("taskDoneTime").setValue(ServerValue.TIMESTAMP);
                                            dbReferenceTasks.child(taskID).child("state").setValue("done");
                                            dbReferenceUser.child(currentUser.getUid()).child("status").setValue("Available");

                                            dbReferenceTasks.child(taskID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    final Task task = dataSnapshot.getValue(Task.class);
                                                    Date assignTime = new Date(task.getTaskAssignTime());
                                                    Date doneTime = new Date(task.getTaskDoneTime());
                                                    Calendar calendar = new GregorianCalendar().getInstance();
                                                    calendar.setTime(assignTime);
                                                    int assignedHour = calendar.get(Calendar.HOUR_OF_DAY);
                                                    int assignedMinute = calendar.get(Calendar.MINUTE);

                                                    calendar.setTime(doneTime);
                                                    int doneHour = calendar.get(Calendar.HOUR_OF_DAY);
                                                    int doneMinute = calendar.get(Calendar.MINUTE);
                                                    final int differenceHour = doneHour-assignedHour;
                                                    final int differenceMinute = doneMinute-assignedMinute + (differenceHour*60);


                                                    dbReferenceTaskAverage.child(task.getType()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            TasksAverage tasksAverage = dataSnapshot.getValue(TasksAverage.class);
                                                            int tempCounterValue = tasksAverage.getCounter();

                                                                int tempAverageMinute = tasksAverage.getMinute();

                                                                if (differenceMinute <= tempAverageMinute - 5) {
                                                                    dbReferenceTasks.child(taskID).child("evaluation").setValue("good");
                                                                } else if (differenceMinute >= tempAverageMinute + 5) {
                                                                    dbReferenceTasks.child(taskID).child("evaluation").setValue("bad");
                                                                } else {
                                                                    dbReferenceTasks.child(taskID).child("evaluation").setValue("average");
                                                                }

                                                                tempAverageMinute =  tempCounterValue * tempAverageMinute;
                                                                tempAverageMinute = (tempAverageMinute + differenceMinute)/tempCounterValue + 1;

                                                            dbReferenceTaskAverage.child(task.getType()).child("minute").setValue(tempAverageMinute);
                                                            dbReferenceTaskAverage.child(task.getType()).child("counter").setValue(tempCounterValue + 1);
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            Toast.makeText(getContext(), "Task is done.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.show();
                                }
                            }
                        return false;
                    }
                });

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentUser != null && currentUser.getPosition().equals(UserRole.Manager.toString()) && model.getByWho().equals("It is in queue.")){
                            CharSequence optionsManager[];
                                optionsManager = new CharSequence[]{"Update Task", "Assign Task", "Predicted Employee"};
                                dbReferenceUser.child(model.getPredictedEmployee()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        predictedUser = dataSnapshot.getValue(User.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Select Options");

                            builder.setItems(optionsManager, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (i == 0) {
                                        Intent intentUpdateTask = new Intent(getContext(), NewTaskActivity.class);
                                        intentUpdateTask.putExtra("taskID", taskID);
                                        if(!model.getByWho().equals("It is in queue.")) {
                                            intentUpdateTask.putExtra("userID", model.getByWho());
                                        }
                                        startActivity(intentUpdateTask);
                                    }

                                    if (i == 1) {
                                        if(model.getByWho().equals("It is in queue.")) {
                                            Intent intentChooseEmployee = new Intent(getContext(), UsersActivity.class);
                                            intentChooseEmployee.putExtra("taskID", taskID);
                                            startActivity(intentChooseEmployee);
                                        }else Toast.makeText(getContext(), "Task already assigned to an employee.", Toast.LENGTH_SHORT).show();
                                    }

                                    if(i == 2){
                                        if(model.getByWho().equals("It is in queue.")) {

                                           builder.setMessage("Predicted employee is " + predictedUser.getFullName());
                                           builder.setNegativeButton("Exit", null);
                                           builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   dbReferenceTasks.child(taskID).child("byWho").setValue(predictedUser.getUid());
                                                   dbReferenceUser.child(predictedUser.getUid()).child("status").setValue("Busy");
                                               }
                                           });
                                           builder.show();

                                        }else Toast.makeText(getContext(), "Task already assigned to an employee.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_task_layout, viewGroup, false);

                return new TaskViewHolder(view);
            }
        };

        rvTaskList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class TaskViewHolder extends  RecyclerView.ViewHolder{
        View mview;

        private DatabaseReference usersDatabase;
        public TaskViewHolder(View itemView) {
            super(itemView);

           usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
           mview = itemView;
        }

        public void setTasksName(String taskName){
            TextView tvTaskName = mview.findViewById(R.id.tvTaskSingleName);
            tvTaskName.setText(taskName);
        }

        public void setTasksDidByWho(String byWho){

            final TextView tvTaskDidByWho = mview.findViewById(R.id.tvSingleWho);
            if(byWho != null) {
                if (!byWho.equals("It is in queue.") && !TextUtils.isEmpty(byWho)) {
                    usersDatabase.child(byWho).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                tvTaskDidByWho.setText(user.getFullName());
                                final CircleImageView taskPriorityView = mview.findViewById(R.id.ivTaskSingleImage);
                                Picasso.get().load(user.getThumbImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.profile).into(taskPriorityView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(user.getThumbImage()).placeholder(R.mipmap.profile).into(taskPriorityView);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else if (byWho.equals("It is in queue.")) {
                    ImageView taskState = mview.findViewById(R.id.ivTaskStatusIcon);
                    taskState.setVisibility(View.VISIBLE);
                    taskState.setImageResource(R.mipmap.task_in_queue);
                    tvTaskDidByWho.setText("It is in queue.");
                }
            }
        }

        public void setTasksPriority(String priority){
            ImageView taskState = mview.findViewById(R.id.ivTaskStatusIcon);
            taskState.setVisibility(View.VISIBLE);

            if(priority != null){
            if(priority.equals("Urgent")) {
                taskState.setColorFilter(Color.parseColor("#FF001B"));
            }else if(priority.equals("Important")){
                taskState.setColorFilter(Color.parseColor("#FF9E00"));
            }else if(priority.equals("Standard")){
                taskState.setColorFilter(Color.parseColor("#0067FF"));
            }
            }
        }

        public void setTasksState(String state){
            if(state != null) {
                CircleImageView taskPriorityView = mview.findViewById(R.id.ivTaskSingleImage);
                taskPriorityView.setImageResource(R.mipmap.task_default_image);
                ImageView taskState = mview.findViewById(R.id.ivTaskStatusIcon);
                taskState.setVisibility(View.VISIBLE);

                if (state.equals("waiting")) {
                    taskState.setImageResource(R.mipmap.task_in_progress);
                }

                if (state.equals("done")) {
                    taskState.setImageResource(R.mipmap.task_done);
                }
            }
        }


    }

}
