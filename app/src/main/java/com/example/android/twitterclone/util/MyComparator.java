package com.example.android.twitterclone.util;


import com.example.android.twitterclone.model.Tweet;

import java.util.Comparator;

public class MyComparator implements Comparator<Tweet> {

    @Override
    public int compare(Tweet o1, Tweet o2) {
        if (o1.getTime() > o2.getTime()) {
            return -1;
        } else if (o1.getTime() < o2.getTime()) {
            return 1;
        }
        return 0;
    }}