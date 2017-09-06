package com.example.android.twitterclone.model;

import java.util.Date;

public class Tweet {

    private String user;
    private String content;
    private long time;

    public Tweet(){}

    public Tweet(String user, String content)
    {
        this.user = user;
        this.content = content;
        this.time = new Date().getTime();
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }
}
