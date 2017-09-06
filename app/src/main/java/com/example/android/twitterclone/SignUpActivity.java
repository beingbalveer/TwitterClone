package com.example.android.twitterclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.twitterclone.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText userName, userEmail, userPassword;
    Button loginButton;
    LinearLayout layout;
    ProgressBar progressBar;

    DatabaseReference userRef;
    DatabaseReference emailUserRef;
    DatabaseReference followref;
    DatabaseReference followerref;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = (EditText) findViewById(R.id.user_name);
        userEmail = (EditText) findViewById(R.id.user_email);
        userPassword = (EditText) findViewById(R.id.user_password);
        loginButton = (Button) findViewById(R.id.login_button);
        layout = (LinearLayout) findViewById(R.id.layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        emailUserRef = FirebaseDatabase.getInstance().getReference("email-user");
        followref = FirebaseDatabase.getInstance().getReference("following");
        followerref = FirebaseDatabase.getInstance().getReference("follower");

    }

    public void signin(View view) {
        final String name = userName.getText().toString();
        final String email = userEmail.getText().toString();
        final String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(name))
            Toast.makeText(this, "username cannot be empty", Toast.LENGTH_SHORT).show();

        else if (TextUtils.isEmpty(email))
            Toast.makeText(this, "email cannot be empty", Toast.LENGTH_SHORT).show();

        else if (!email.contains("@"))
            Toast.makeText(this, "invalid email", Toast.LENGTH_SHORT).show();

        else if (TextUtils.isEmpty(password))
            Toast.makeText(this, "password cannot be empty", Toast.LENGTH_SHORT).show();

        else if (password.length() < 6)
            Toast.makeText(this, "password must be 6 character long", Toast.LENGTH_SHORT).show();

        else {
            progressBar.setVisibility(View.VISIBLE);
            userRef.orderByKey().equalTo(name).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(email, name);
                                    userRef.child(name).setValue(user);
                                    String s = email.replace('.', '_');
                                    emailUserRef.child(s).setValue(name);

                                    followref.child(name).child(name).setValue(false);
                                    followerref.child(name).child(name).setValue(false);
                                    Intent intent = new Intent(SignUpActivity.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                } else {
                                    Snackbar.make(layout, "error in creating account", Snackbar.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        Snackbar.make(layout, "username already exists", Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
