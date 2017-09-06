package com.example.android.twitterclone.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.twitterclone.R;
import com.example.android.twitterclone.model.Notification;
import com.example.android.twitterclone.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SearchFragment extends Fragment {

    DatabaseReference reference;
    DatabaseReference followRef;
    DatabaseReference followerRef;
    DatabaseReference notiRef;
    String userName = "";

    public SearchFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference = FirebaseDatabase.getInstance().getReference("users");
        followerRef = FirebaseDatabase.getInstance().getReference("follower");
        followRef = FirebaseDatabase.getInstance().getReference("following");
        notiRef = FirebaseDatabase.getInstance().getReference("notification");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        final EditText input = (EditText) view.findViewById(R.id.search_input);
        Button button = (Button) view.findViewById(R.id.search_button);
        final TextView textView = (TextView) view.findViewById(R.id.text);
        final Button follow_button = (Button) view.findViewById(R.id.follow_button);


        final String currentUser = getArguments().getString("username");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                reference.orderByKey().equalTo(input.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot db : dataSnapshot.getChildren()) {

                                User user = db.getValue(User.class);
                                textView.setText(user.getName());
                                userName = user.getName();


                                followRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(userName)) {
                                            for (DataSnapshot db : dataSnapshot.getChildren()) {
                                                String key = db.getKey();
                                                if (key.equals(userName)) {
                                                    Boolean b = db.getValue(Boolean.class);
                                                    if (b) {
                                                        follow_button.setText("unfollow");
                                                    } else {
                                                        follow_button.setText("follow");
                                                    }
                                                }
                                            }
                                        } else {
                                            followRef.child(currentUser).child(userName).setValue(false);
                                            follow_button.setText("follow");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                follow_button.setVisibility(View.VISIBLE);

                            }

                        } else
                            Snackbar.make(view, "no user found", Snackbar.LENGTH_LONG).show();

                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (follow_button.getText().toString().equals("follow")) {
                    followRef.child(currentUser).child(userName).setValue(true);
                    followerRef.child(userName).child(currentUser).setValue(true);
                    notiRef.child(userName).push().setValue(new Notification(currentUser + " follow you"));
                    follow_button.setText("unfollow");
                } else {
                    followRef.child(currentUser).child(userName).setValue(false);
                    followerRef.child(userName).child(currentUser).setValue(false);
                    notiRef.child(userName).push().setValue(new Notification(currentUser + " unfollow you"));
                    follow_button.setText("follow");
                }
            }
        });

        return view;
    }
}
