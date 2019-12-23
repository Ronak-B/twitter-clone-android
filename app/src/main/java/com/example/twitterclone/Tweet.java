package com.example.twitterclone;

public class Tweet {
    private String username;
    private String handle;
    private String tweettext;
    private String date;
    private String fname;

    public Tweet(String username, String handle, String tweettext, String date,String fname) {
        this.username = username;
        this.handle = handle;
        this.tweettext = tweettext;
        this.date = date;
        this.fname=fname;
    }

    public String getUsername() {
        return username;
    }

    public String getFname() {
        return fname;
    }

    public String getHandle() {
        return handle;
    }

    public String getTweettext() {
        return tweettext;
    }

    public String getDate() {
        return date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setTweettext(String tweettext) {
        this.tweettext = tweettext;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
