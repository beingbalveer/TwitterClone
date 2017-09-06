package com.example.android.twitterclone.fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.twitterclone.adapters.FollowerAdapter;
import com.example.android.twitterclone.R;
import com.example.android.twitterclone.model.Comment;
import com.example.android.twitterclone.model.Tweet;
import com.example.android.twitterclone.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference reference;
    DatabaseReference followingRef;
    DatabaseReference followerRef;
    DatabaseReference commentRef;

    ArrayList<String> keyList = new ArrayList<>(0);

    String name;
    String profilePic;


    ArrayList<String> followingList = new ArrayList<>(0);
    ArrayList<String> followerList = new ArrayList<>(0);


    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        followingRef = FirebaseDatabase.getInstance().getReference("following");
        followerRef = FirebaseDatabase.getInstance().getReference("follower");
        commentRef = FirebaseDatabase.getInstance().getReference("comments");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final String username = getArguments().getString("username");

        final ListView listView_tweets = (ListView) view.findViewById(R.id.listview_tweets);
        final ListView listView_following = (ListView) view.findViewById(R.id.listview_following);
        final ListView listView_follower = (ListView) view.findViewById(R.id.listview_follower);
        listView_following.setVisibility(View.INVISIBLE);
        listView_follower.setVisibility(View.INVISIBLE);

        final CircleImageView imageView = (CircleImageView) view.findViewById(R.id.profilePic);
        final TextView textView = (TextView) view.findViewById(R.id.user_name);

        reference.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user1 = dataSnapshot.getValue(User.class);
                name = user1.getName();
                profilePic = user1.getProfilePic();

                textView.setText(username);

                if (!profilePic.isEmpty()) {
                    byte[] data = Base64.decode(profilePic, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ///////     tweet section       //////////


        final DatabaseReference reference_tweet = FirebaseDatabase.getInstance().getReference("tweet-list");
        final FirebaseListAdapter adapter = new FirebaseListAdapter<Tweet>(getActivity(), Tweet.class, R.layout.one_tweet_layout, reference_tweet.child(username)) {
            @Override
            protected void populateView(View v, Tweet model, int position) {
                TextView user_text = (TextView) v.findViewById(R.id.user_name);
                TextView content_text = (TextView) v.findViewById(R.id.tweet_content);
                TextView time_text = (TextView) v.findViewById(R.id.time);

                user_text.setText(model.getUser());
                content_text.setText(model.getContent());
                String s = findTime(model.getTime());
                time_text.setText(s);
            }

            @Override
            public Tweet getItem(int position) {
                return super.getItem(getCount() - 1 - position);
            }

        };
        listView_tweets.setAdapter(adapter);
        listView_tweets.setNestedScrollingEnabled(true);
        adapter.notifyDataSetChanged();


        ///////////         long click handling         /////////////

        listView_tweets.setLongClickable(true);
        listView_tweets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                PopupMenu menu = new PopupMenu(getContext(), view, Gravity.END);
                menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.delete) {
                            DatabaseReference deleteRef = adapter.getRef(adapter.getCount() - 1 - position);
                            deleteRef.removeValue();

                        }
                        return false;
                    }

                });

                return true;
            }
        });


        /////////           single click handling           ///////////////////

        listView_tweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.popup_tweet_layout, null);
                dialog.setView(dialogView);

                Tweet tweet = (Tweet) listView_tweets.getItemAtPosition(position);

                TextView user_text = (TextView) dialogView.findViewById(R.id.user_name);
                TextView content_text = (TextView) dialogView.findViewById(R.id.tweet_content);
                user_text.setText(tweet.getUser());
                content_text.setText(tweet.getContent());
                final long time = tweet.getTime();

                final EditText comment_input = (EditText) dialogView.findViewById(R.id.comment_input);
                FloatingActionButton comment_button = (FloatingActionButton) dialogView.findViewById(R.id.comment_button);
                final ListView commentListView = (ListView) dialogView.findViewById(R.id.listview);

                final FirebaseListAdapter<Comment> adapter1 = new FirebaseListAdapter<Comment>(getActivity(), Comment.class, R.layout.one_comment_layout, commentRef.child("" + time)) {
                    @Override
                    protected void populateView(View v, Comment model, int position) {

                        TextView user_text = (TextView) v.findViewById(R.id.user_name);
                        TextView comment_text = (TextView) v.findViewById(R.id.comment_content);
                        TextView time_text = (TextView) v.findViewById(R.id.time);

                        user_text.setText(model.getUser());
                        comment_text.setText(model.getContent());
                        String time = findTime(model.getTime());
                        time_text.setText(time);


                    }

                    @Override
                    public Comment getItem(int position) {
                        return super.getItem(getCount() - 1 - position);
                    }
                };

                commentListView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();


                commentListView.setLongClickable(true);
                commentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                        PopupMenu menu = new PopupMenu(getContext(), view, Gravity.END);
                        menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());
                        menu.show();
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                if (id == R.id.delete) {
                                    DatabaseReference deleteRef = adapter1.getRef(adapter1.getCount() - 1 - position);
                                    deleteRef.removeValue();

                                }
                                return false;
                            }

                        });

                        return true;
                    }
                });



                comment_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String comment_content = comment_input.getText().toString();
                        comment_input.setText("");
                        Comment comment = new Comment(username, comment_content);
                        commentRef.child("" + time).push().setValue(comment);
                        adapter1.notifyDataSetChanged();


                    }
                });

                dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        /////////       following section       ////////

        followingRef.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    boolean value = db.getValue(Boolean.class);
                    String key = db.getKey();

                    if (value)
                        followingList.add(key);
                }

                if (getActivity() != null) {
                    FollowerAdapter adapter1 = new FollowerAdapter(followingList);
                    listView_following.setAdapter(adapter1);
                    listView_tweets.setNestedScrollingEnabled(true);
                    adapter1.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        /////////       follower section        //////////

        followerRef.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    boolean value = db.getValue(Boolean.class);
                    String key = db.getKey();

                    if (value)
                        followerList.add(key);
                }

                if (getActivity() != null) {
                    FollowerAdapter adapter1 = new FollowerAdapter(followerList);
                    listView_follower.setAdapter(adapter1);
                    listView_tweets.setNestedScrollingEnabled(true);
                    adapter1.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getText().equals("Tweet")) {
                    listView_tweets.setVisibility(View.VISIBLE);
                    listView_follower.setVisibility(View.INVISIBLE);
                    listView_following.setVisibility(View.INVISIBLE);

                } else if (tab.getText().equals("Follower")) {
                    listView_tweets.setVisibility(View.INVISIBLE);
                    listView_following.setVisibility(View.INVISIBLE);
                    listView_follower.setVisibility(View.VISIBLE);

                } else if (tab.getText().equals("Following")) {
                    listView_tweets.setVisibility(View.INVISIBLE);
                    listView_follower.setVisibility(View.INVISIBLE);
                    listView_following.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        return view;
    }

    private String findTime(long tweetTime) {
        long currentTime = new Date().getTime();
        long d = (currentTime - tweetTime) / 1000;

        if (d < 60)
            return "" + d + "sec";
        else if (d < 3600) {
            long m = d / 60;
            return "" + m + "min";
        } else if (d < 86400) {
            long h = d / 3600;
            return "" + h + "h";
        } else if (d < 2592000) {
            long h = d / 86400;
            return "" + h + "D";
        }
        return "";
    }


}
