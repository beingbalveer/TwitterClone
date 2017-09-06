package com.example.android.twitterclone.model;

import java.util.Date;

public class Comment {

    private String user;
    private String content;
    private long time;

    public Comment(){}

    public Comment(String user, String content)
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
