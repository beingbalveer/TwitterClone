package com.example.android.twitterclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText userEmail, userPassword;
    Button loginButton;
    LinearLayout layout;
    ProgressBar progressBar;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmail = (EditText)findViewById(R.id.user_email);
        userPassword = (EditText)findViewById(R.id.user_password);
        loginButton = (Button) findViewById(R.id.login_button);
        layout = (LinearLayout) findViewById(R.id.layout);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("email-user");
    }

    public void signin(View view) {
        final String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email))
            Toast.makeText(this,"email cannot be empty",Toast.LENGTH_SHORT).show();

        else if (!email.contains("@"))
            Toast.makeText(this,"invalid email",Toast.LENGTH_SHORT).show();

        else if (TextUtils.isEmpty(password))
            Toast.makeText(this,"password cannot be empty",Toast.LENGTH_SHORT).show();

        else if (password.length()<6)
            Toast.makeText(this,"password must be 6 character long",Toast.LENGTH_SHORT).show();

        else
        {
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Intent intent = new Intent(MainActivity.this,Home.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"error in login",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    public void gotoSugnUp(View view) {

        Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
        startActivity(intent);
    }


}
