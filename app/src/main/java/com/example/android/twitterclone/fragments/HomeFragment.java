package com.example.android.twitterclone.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.twitterclone.util.MyComparator;
import com.example.android.twitterclone.R;
import com.example.android.twitterclone.adapters.TweetAdapter;
import com.example.android.twitterclone.model.Comment;
import com.example.android.twitterclone.model.Notification;
import com.example.android.twitterclone.model.Tweet;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class HomeFragment extends Fragment {

    DatabaseReference reference;
    DatabaseReference followRef;
    DatabaseReference commentRef;
    DatabaseReference notiRef;

    ArrayList<Tweet> tweetList = new ArrayList<>(0);
    ArrayList<String> followerList = new ArrayList<>(0);
    ArrayList<String> commentLIst = new ArrayList<>(0);



    String tag = "tweets";

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference("tweet-list");
        followRef = FirebaseDatabase.getInstance().getReference("following");
        commentRef = FirebaseDatabase.getInstance().getReference("comments");
        notiRef = FirebaseDatabase.getInstance().getReference("notification");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        final ListView listView = (ListView) view.findViewById(R.id.listview);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        final String username = getArguments().getString("username");
        final TweetAdapter[] adapter = new TweetAdapter[1];

        followRef.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot db : dataSnapshot.getChildren()) {
                    String key = db.getKey();
                    Boolean b = db.getValue(Boolean.class);
                    if (b)
                        followerList.add(key);
                }

                if (followerList.size() != 0) {


                    for (int i = 0; i < followerList.size(); i++) {
                        final int j = i;
                        reference.child(followerList.get(i)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot db : dataSnapshot.getChildren()) {
                                    Tweet tweet = db.getValue(Tweet.class);
                                    tweetList.add(tweet);
                                }

                                if (j == followerList.size() - 1) {
                                    Collections.sort(tweetList, new MyComparator());

                                    adapter[0] = new TweetAdapter(tweetList);
                                    listView.setAdapter(adapter[0]);
                                    adapter[0].notifyDataSetChanged();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (getContext()!=null)
                        Toast.makeText(getContext(),"follow somebody to read tweets",Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //////////          item    click   ///////////

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View dialogView = inflater.inflate(R.layout.popup_tweet_layout, null);
                dialog.setView(dialogView);

                final Tweet tweet = (Tweet) listView.getItemAtPosition(position);

                TextView user_text = (TextView) dialogView.findViewById(R.id.user_name);
                TextView content_text = (TextView) dialogView.findViewById(R.id.tweet_content);
                user_text.setText(tweet.getUser());
                content_text.setText(tweet.getContent());
                final long time = tweet.getTime();

                final EditText comment_input = (EditText)dialogView.findViewById(R.id.comment_input);
                FloatingActionButton comment_button = (FloatingActionButton)dialogView.findViewById(R.id.comment_button);
                final ListView commentListView = (ListView)dialogView.findViewById(R.id.listview);

                final FirebaseListAdapter<Comment> adapter1 = new FirebaseListAdapter<Comment>(getActivity(),Comment.class,R.layout.one_comment_layout,commentRef.child(""+time)) {
                    @Override
                    protected void populateView(View v, Comment model, int position) {

                        TextView user_text = (TextView)v.findViewById(R.id.user_name);
                        TextView comment_text = (TextView)v.findViewById(R.id.comment_content);
                        TextView time_text = (TextView)v.findViewById(R.id.time);

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


                ///////         long click to delete comment        /////////

                commentListView.setLongClickable(true);
                commentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                        Comment comment = (Comment) commentListView.getItemAtPosition(position);

                        PopupMenu menu = new PopupMenu(getContext(), view, Gravity.END);
                        menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());

                        if (comment.getUser().equals(username))
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
                        Comment comment = new Comment(username,comment_content);
                        commentRef.child("" + time).push().setValue(comment);
                        adapter1.notifyDataSetChanged();

                        notiRef.child(tweet.getUser()).push().setValue(new Notification(username + " commented on your tweet"));


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
