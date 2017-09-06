package com.example.android.twitterclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.twitterclone.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileView;
    TextView nameView, emailView, phoneView;
    Button editButton;
    ProgressBar progressBar;
    ConstraintLayout layout;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    String name,email,phone;
    static String profilePic;
    public static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        profileView = (CircleImageView)findViewById(R.id.profile_picture);
        nameView = (TextView)findViewById(R.id.name);
        emailView = (TextView)findViewById(R.id.email);
        phoneView = (TextView)findViewById(R.id.phone);
        editButton = (Button)findViewById(R.id.edit_profile_button);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        layout = (ConstraintLayout)findViewById(R.id.profile_layout);

        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        Intent intent = getIntent();
        String userName = intent.getExtras().getString("name");

        reference.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);
                name = user1.getName();
                email = user1.getEmail();
                phone = user1.getPhone();
                profilePic = user1.getProfilePic();

                nameView.setText(name);
                emailView.setText(email);
                phoneView.setText(phone);
                byte[] data = Base64.decode(profilePic, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                profileView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("phone",phone);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        finish();
        return true;
    }

}



