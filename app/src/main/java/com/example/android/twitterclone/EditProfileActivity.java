package com.example.android.twitterclone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

import static com.example.android.twitterclone.ProfileActivity.profilePic;

public class EditProfileActivity extends AppCompatActivity {

    ImageView profileView;
    EditText emailView, phoneView;
    TextView nameView;
    Button saveButton;
    String new_email;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    String name, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        profileView = (ImageView) findViewById(R.id.profile_picture);
        nameView = (TextView) findViewById(R.id.name);
        emailView = (EditText) findViewById(R.id.email);
        phoneView = (EditText) findViewById(R.id.phone);
        saveButton = (Button) findViewById(R.id.save_profile_button);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        new_email = user.getEmail();
        new_email = new_email.replace('.', '_');

        Intent intent = getIntent();
        name = intent.getExtras().getString("name");
        email = intent.getExtras().getString("email");
        phone = intent.getExtras().getString("phone");

        nameView.setText(name);
        emailView.setText(email);
        phoneView.setText(phone);
        profileView.setImageBitmap(BitmapFactory.decodeFile(profilePic));

    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        finish();
        return true;
    }

    public void saveProfile(View view) {

        email = emailView.getText().toString();
        phone = phoneView.getText().toString();

        String profileString = getImageAsString();
        if (!profileString.equals(profilePic) && !profileString.isEmpty())
            reference.child(name).child("profilePic").setValue(profileString);

        reference.child(name).child("phone").setValue(phone);

        if (!email.equals(user.getEmail()) && user != null) {
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "email update successfull", Toast.LENGTH_LONG).show();
                        reference.child(name).child("email").setValue(email);
                    } else {
                        Toast.makeText(EditProfileActivity.this, "email update failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else
            finish();
    }


    private String getImageAsString() {
        BitmapDrawable drawable = (BitmapDrawable) profileView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return "";
    }


    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 200);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Toast.makeText(this, "image " + columnIndex, Toast.LENGTH_LONG).show();
                profileView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

            } else
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show();

        }
    }

}



