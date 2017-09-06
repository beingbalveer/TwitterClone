package com.example.android.twitterclone.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.twitterclone.R;
import com.example.android.twitterclone.model.Tweet;

import java.util.ArrayList;
import java.util.Date;

public class TweetAdapter extends BaseAdapter {

    Context context;
    ArrayList<Tweet> tweets;

    public TweetAdapter(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public int getCount() {
        return tweets.size();
    }

    @Override
    public Object getItem(int position) {
        return tweets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_tweet_layout, parent, false);

        TextView user_text = (TextView) convertView.findViewById(R.id.user_name);
        TextView content_text = (TextView) convertView.findViewById(R.id.tweet_content);
        TextView time_text = (TextView) convertView.findViewById(R.id.time);

        Tweet tweet = tweets.get(position);
        user_text.setText(tweet.getUser());
        content_text.setText(tweet.getContent());

        String s = findTime(tweet.getTime());
        time_text.setText(s);

        return convertView;
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
