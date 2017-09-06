package com.example.android.twitterclone.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.android.twitterclone.R;

public class CustomAdapter extends BaseAdapter{

    Context context;

    CustomAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_tweet_layout,parent,false);

        return convertView;
    }
}
