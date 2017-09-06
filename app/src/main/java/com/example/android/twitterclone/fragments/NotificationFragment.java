package com.example.android.twitterclone.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.twitterclone.R;
import com.example.android.twitterclone.model.Notification;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotificationFragment extends Fragment {

    DatabaseReference notiRef;

    public NotificationFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notiRef = FirebaseDatabase.getInstance().getReference("notification");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        final String currentUser = getArguments().getString("username");

        final FirebaseListAdapter<Notification> adapter = new FirebaseListAdapter<Notification>(getActivity(), Notification.class, R.layout.one_follower_layout, notiRef.child(currentUser)) {
            @Override
            protected void populateView(View v, Notification model, int position) {

                CircleImageView imageView = (CircleImageView) v.findViewById(R.id.profilePic);
                TextView title_text = (TextView) v.findViewById(R.id.user_name);
                TextView time_text = (TextView) v.findViewById(R.id.time_text);

                title_text.setText(model.getTitle());
                imageView.setImageResource(R.drawable.ic_notifications_black_24dp);

                String s = findTime(model.getTime());
                time_text.setText(s);

            }

            @Override
            public Notification getItem(int position) {
                return super.getItem(getCount() - 1 - position);
            }
        };

        ListView listview = (ListView) view.findViewById(R.id.listview);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                            DatabaseReference deleteRef = adapter.getRef(position);
                            deleteRef.removeValue();

                        }
                        return false;
                    }

                });

                return true;
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
