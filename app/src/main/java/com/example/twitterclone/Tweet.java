package com.example.twitterclone;

public class Tweet {
    private String username;
    private String handle;
    private String tweettext;
    private String date;

    public Tweet(String username, String handle, String tweettext, String date) {
        this.username = username;
        this.handle = handle;
        this.tweettext = tweettext;
        this.date = date;
    }

    public String getUsername() {
        return username;
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
