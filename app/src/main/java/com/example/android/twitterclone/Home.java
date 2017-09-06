package com.example.android.twitterclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.twitterclone.fragments.HomeFragment;
import com.example.android.twitterclone.fragments.NotificationFragment;
import com.example.android.twitterclone.fragments.ProfileFragment;
import com.example.android.twitterclone.fragments.SearchFragment;
import com.example.android.twitterclone.model.Tweet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.Date;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference reference;
    DatabaseReference ref;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("email-user");

        reference.orderByKey().equalTo(currentUser.getEmail().replace('.','_')).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child(currentUser.getEmail().replace('.','_')).getValue(String.class);
                if (getSupportActionBar()!=null)
                    getSupportActionBar().setTitle(userName);
                ref = FirebaseDatabase.getInstance().getReference("tweet-list").child(userName);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username",userName);
                fragment.setArguments(bundle);
                transaction.add(R.id.fragment_place, fragment);
                transaction.commit();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ///////     fragment part       //////////
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment;
                Bundle bundle = new Bundle();
                bundle.putString("username",userName);

                switch (id) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.fragment_place, fragment);
                        break;
                    case R.id.notification:

                        fragment = new NotificationFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.fragment_place, fragment);
                        break;
                    case R.id.search:
                        fragment = new SearchFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.fragment_place, fragment);
                        break;
                    case R.id.profile:
                        fragment = new ProfileFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.fragment_place, fragment);
                        break;
                }
                transaction.commit();
                return true;
            }
        });

    }

    public void newTweet() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_tweet_view, null);
        dialog.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.new_tweet_content);

        dialog.setPositiveButton("Tweet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = editText.getText().toString();
                Tweet tweet = new Tweet(userName,content);
                ref.child("" + new Date().getTime()).setValue(tweet);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_tweet)
            newTweet();
        else if (id == R.id.search)
            search();
        else if (id == R.id.setting)
            setting();
        else if (id == R.id.logout)
            logout();
        else if (id == R.id.profile)
            gotoProfile();
        return true;
    }

    void search(){}

    void setting(){}

    void gotoProfile()
    {
        Intent intent = new Intent(Home.this,ProfileActivity.class);
        intent.putExtra("name",userName);
        startActivity(intent);
    }

    void logout()
    {
        auth.signOut();
        if (auth.getCurrentUser()==null)
        {
            Intent intent = new Intent(Home.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(this,"error in log out",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}


class BottomNavigationViewHelper {

    static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }
}

