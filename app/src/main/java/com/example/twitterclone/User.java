package com.example.twitterclone;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String username;
    private String handle;
    private String email;
    private String dateJoined;

    public User(String id,String username, String handle, String email, String dateJoined) {
        this.id=id;
        this.username = username;
        this.handle = handle;
        this.email = email;
        this.dateJoined = dateJoined;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }
}
