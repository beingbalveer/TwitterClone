package com.example.android.twitterclone.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.twitterclone.R;

import java.util.ArrayList;


// it is used for both follower and following list
public class FollowerAdapter extends BaseAdapter{

    ArrayList<String> followingList;

    public FollowerAdapter(ArrayList<String> followingList)
    {
        this.followingList = followingList;
    }

    @Override
    public int getCount() {
        return followingList.size();
    }

    @Override
    public Object getItem(int position) {
        return followingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_follower_layout,parent,false);

        TextView user_text = (TextView)convertView.findViewById(R.id.user_name);
        user_text.setText(followingList.get(position));

        return convertView;
    }
}
