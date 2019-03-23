package com.example.hemsapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
            query = tasksDatabaseReference.orderByChild("state").equalTo(true);
        }

        FirebaseRecyclerOptions<Task> options =
                new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(query,Task.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder holder, int position, @NonNull Task model) {

                holder.setTasksName(model.getName());
                holder.setTasksPriority();
                holder.setTasksState(model.getState());
                holder.setTasksDidByWho(model.getByWho());
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

        public TaskViewHolder(View itemView) {
            super(itemView);
           mview = itemView;
        }

        public void setTasksName(String taskName){
            TextView tvTaskName = mview.findViewById(R.id.tvTaskSingleName);
            tvTaskName.setText(taskName);
        }

        public void setTasksDidByWho(String byWho){
            TextView tvTaskDidByWho = mview.findViewById(R.id.tvSingleWho);
            tvTaskDidByWho.setText(byWho);
        }

        //TODO: Takae status from database and change color case by case
        public void setTasksState(boolean state){
            ImageView taskState = mview.findViewById(R.id.ivTaskStatusIcon);
            taskState.setVisibility(View.VISIBLE);
            taskState.setColorFilter(Color.BLUE);
        }

        public void setTasksPriority(){
            CircleImageView taskPriorityView = mview.findViewById(R.id.ivTaskSingleImage);
            taskPriorityView.setImageResource(R.mipmap.chat_add_button);
        }
    }

}
