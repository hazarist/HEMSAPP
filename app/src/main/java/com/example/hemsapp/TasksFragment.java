package com.example.hemsapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment {

    private View taskView;
    private RecyclerView taskList;
    private DatabaseReference tasksDatabaseReference;
    private FirebaseRecyclerAdapter<Task,TaskViewHolder> firebaseRecyclerAdapter;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        taskView = inflater.inflate(R.layout.fragment_tasks, container, false);

        taskList = taskView.findViewById(R.id.task_list);

        tasksDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Tasks");
        tasksDatabaseReference.keepSynced(true);

        taskList.setHasFixedSize(true);
        taskList.setLayoutManager(new LinearLayoutManager(getContext()));

        return taskView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query;

        if(LoginActivity.staticUser.getPosition().equals(UserRole.Employee.toString())) {
            query = tasksDatabaseReference; //TODO: order by their priority
        }else {
            query = tasksDatabaseReference;
        }

        FirebaseRecyclerOptions<Task> options =
                new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(query,Task.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Task model) {
                holder.setTasksName(model.getName());
                holder.setTasksPriority(model.getPriority());
                holder.setTasksState(model.getState());
                holder.setTasksDidByWho(model.getByWho());
                final String taskID = getRef(position).getKey();
                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Take Task", "Look Task"};
                        CharSequence optionsManager[] = new CharSequence[]{"Update Task", "Assign Task"};
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("Select Options");



                        if(LoginActivity.staticUser.getPosition().equals(UserRole.Employee.toString())) {
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (i == 0) {
//                                        Intent intentProfile = new Intent(getContext(), ProfileActivity.class);
//                                        intentProfile.putExtra("userID", userID);
//                                        startActivity(intentProfile);
                                        Toast.makeText(getContext(),"Employee Take Task",Toast.LENGTH_SHORT).show();
                                    }

                                    if (i == 1) {
//                                        Intent intentChat = new Intent(getContext(), ChatActivity.class);
//                                        intentChat.putExtra("userID", userID);
//                                        intentChat.putExtra("userName", userName);
//                                        startActivity(intentChat);
                                        Toast.makeText(getContext(),"Employee Look Task",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else if(LoginActivity.staticUser.getPosition().equals(UserRole.Manager.toString())){
                            builder.setItems(optionsManager, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (i == 0) {
//                                        Intent intentProfile = new Intent(getContext(), ProfileActivity.class);
//                                        intentProfile.putExtra("userID", userID);
//                                        startActivity(intentProfile);
                                        Toast.makeText(getContext(),"Manager Update Task",Toast.LENGTH_SHORT).show();
                                    }

                                    if (i == 1) {
                                        Intent intentChooseEmployee = new Intent(getContext(), UsersActivity.class);
                                        intentChooseEmployee.putExtra("taskID", taskID);
                                        startActivity(intentChooseEmployee);
                                    }
                                }
                            });
                        }

                        builder.show();
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

        taskList.setAdapter(firebaseRecyclerAdapter);
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
            if(!byWho.equals("It is in queue.") && !TextUtils.isEmpty(byWho)) {
                usersDatabase.child(byWho).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        tvTaskDidByWho.setText(user.getFullName());
                        CircleImageView taskPriorityView = mview.findViewById(R.id.ivTaskSingleImage);
                        Picasso.get().load(user.getThumbImage()).placeholder(R.mipmap.profile).into(taskPriorityView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }else if(byWho.equals("It is in queue.")){
                tvTaskDidByWho.setText("It is in queue.");
            }
        }

        //TODO: Takae status from database and change color case by case
        public void setTasksPriority(String priority){
            ImageView taskState = mview.findViewById(R.id.ivTaskStatusIcon);
            taskState.setVisibility(View.VISIBLE);
            if(priority.equals("Urgent")) {
                taskState.setColorFilter(Color.RED);
            }else if(priority.equals("Important")){
                taskState.setColorFilter(Color.YELLOW);
            }else if(priority.equals("Standard")){
                taskState.setColorFilter(Color.GREEN);
            }
        }

        public void setTasksState(String state){
            if(!state.equals("inProcess")) {
                CircleImageView taskPriorityView = mview.findViewById(R.id.ivTaskSingleImage);
                if (state.equals("waiting")) {
                    taskPriorityView.setImageResource(R.mipmap.task_state_in_process);
                } else if (state.equals("done")) {
                    taskPriorityView.setImageResource(R.mipmap.task_state_done);
                }
            }
        }


    }

}
