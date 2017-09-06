package com.example.android.twitterclone.model;

public class User {

    private String email;
    private String name;
    private String phone = "";
    private String profilePic = "";

    public User(){}

    public User(String email, String name)
    {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
