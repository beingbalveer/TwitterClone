package com.example.android.twitterclone.model;

import java.util.Date;

public class Notification {

    private String title = "";
    private long time;

    public Notification(){}

    public Notification(String title)
    {
        this.title = title;
        this.time = new Date().getTime();
    }

    public String getTitle() {
        return title;
    }

    public long getTime() {
        return time;
    }
}
