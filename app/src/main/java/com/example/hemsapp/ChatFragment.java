 package com.example.hemsapp;

 import android.content.Intent;
 import android.graphics.Typeface;
 import android.os.Bundle;
 import android.os.Message;
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

 import com.firebase.ui.database.FirebaseRecyclerAdapter;
 import com.firebase.ui.database.FirebaseRecyclerOptions;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.database.ChildEventListener;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.Query;
 import com.google.firebase.database.ValueEventListener;
 import com.squareup.picasso.Callback;
 import com.squareup.picasso.NetworkPolicy;
 import com.squareup.picasso.Picasso;

 import java.util.Map;

 import de.hdodenhof.circleimageview.CircleImageView;


 /**
  * A simple {@link Fragment} subclass.
  */
 public class ChatFragment extends Fragment {

     private RecyclerView mConvList;
     private FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseRecyclerAdapter;
     private DatabaseReference mConvDatabase;
     private DatabaseReference mMessageDatabase;
     private DatabaseReference mUsersDatabase;

     private FirebaseAuth mAuth;

     private String mCurrent_user_id;

     private View mMainView;


     public ChatFragment() {
         // Required empty public constructor
     }


     @Override
     public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

         mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

         mConvList = mMainView.findViewById(R.id.conv_list);
         mAuth = FirebaseAuth.getInstance();

         if(mAuth != null && mAuth.getCurrentUser() != null)
         mCurrent_user_id = mAuth.getCurrentUser().getUid();

         mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
         mConvDatabase.keepSynced(true);

         mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
         mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);
         mUsersDatabase.keepSynced(true);

         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
         linearLayoutManager.setReverseLayout(true);
         linearLayoutManager.setStackFromEnd(true);

         mConvList.setHasFixedSize(true);
         mConvList.setLayoutManager(linearLayoutManager);


         // Inflate the layout for this fragment
         return mMainView;
     }


     @Override
     public void onStart() {
         super.onStart();

         Query query = mConvDatabase.orderByChild("timestamp");


         final FirebaseRecyclerOptions<Conv> options=
                 new FirebaseRecyclerOptions.Builder<Conv>()
                         .setQuery(query,Conv.class)
                         .build();

            firebaseRecyclerAdapter  = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(options) {
             @Override
             protected void onBindViewHolder(@NonNull final ConvViewHolder convViewHolder, int position, @NonNull final Conv conv) {
                 final String list_user_id = getRef(position).getKey();

                 if(list_user_id != null) {
                     Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                     lastMessageQuery.addChildEventListener(new ChildEventListener() {
                         @Override
                         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                             if(dataSnapshot.child("message").getValue() != null) {
                                 Messages message = dataSnapshot.getValue(Messages.class);
                                 if (message != null && message.getType().equals("text")) {
                                     convViewHolder.setMessage(message.getMessage(), conv.isSeen());
                                 }
                             }
                         }

                         @Override
                         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                         }

                         @Override
                         public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                         }

                         @Override
                         public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });


                     mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                             final User user = dataSnapshot.getValue(User.class);

                             if(user != null) {
                                 convViewHolder.setUserOnline(user.getOnline().toString());
                                 convViewHolder.setName(user.getFullName());
                                 convViewHolder.setUserImage(user.getThumbImage());

                                 convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {

                                         Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                         chatIntent.putExtra("userID", list_user_id);
                                         chatIntent.putExtra("userName", user.getFullName());
                                         startActivity(chatIntent);

                                     }

                                 });
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });
                 }
             }

             @NonNull
             @Override
             public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                 View view = LayoutInflater.from(viewGroup.getContext())
                         .inflate(R.layout.users_single_layout, viewGroup, false);

                 return new ConvViewHolder(view);
             }
         };

         mConvList.setAdapter(firebaseRecyclerAdapter);
         firebaseRecyclerAdapter.startListening();
     }

     @Override
     public void onStop() {
         super.onStop();
         firebaseRecyclerAdapter.stopListening();
     }

     @Override
     public void onResume() {
         super.onResume();
         firebaseRecyclerAdapter.startListening();
     }

     public static class ConvViewHolder extends RecyclerView.ViewHolder {

         View mView;

         public ConvViewHolder(View itemView) {
             super(itemView);

             mView = itemView;

         }

         public void setMessage(String message, boolean isSeen){

             TextView userStatusView = mView.findViewById(R.id.tvSingleStatus);

             userStatusView.setText(message);

             if(!isSeen){
                 userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
             } else {
                 userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
             }

         }

         public void setName(String name){

             TextView userNameView = mView.findViewById(R.id.tvUserSingleName);
             userNameView.setText(name);

         }

         public void setUserImage(final String thumb_image){
             final CircleImageView userImageView = mView.findViewById(R.id.ivUserSingleImage);
             Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.profile).into(userImageView, new Callback() {
                 @Override
                 public void onSuccess() {

                 }

                 @Override
                 public void onError(Exception e) {
                     Picasso.get().load(thumb_image).placeholder(R.mipmap.profile).into(userImageView);
                 }
             });

         }

         public void setUserOnline(String online_status) {

             ImageView userOnlineView = mView.findViewById(R.id.ivOnlineIcon);

             if(online_status.equals("true")){

                 userOnlineView.setVisibility(View.VISIBLE);

             } else {

                 userOnlineView.setVisibility(View.INVISIBLE);

             }

         }


     }



 }