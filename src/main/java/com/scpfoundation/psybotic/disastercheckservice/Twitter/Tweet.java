package com.scpfoundation.psybotic.disastercheckservice.Twitter;


import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

import java.sql.Timestamp;
import java.util.*;

public class Tweet {

    private String text;
    private String user;
    private String language;
    private Date time;
    private List<String> hashtags;
    private List<String> mentions;
    private String id;


    public String getText() {
        return text;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Tweet(Status status) {
        this.text = status.getText();
        this.user = status.getUser().getScreenName();
        this.language = status.getUser().getLang();
        this.id=""+status.getId();
        this.hashtags = new ArrayList();
        this.time=status.getCreatedAt();
        for (HashtagEntity h : status.getHashtagEntities()) {
            this.hashtags.add(h.getText());
        }
        this.mentions = new ArrayList();
        for (UserMentionEntity u : status.getUserMentionEntities()) {
            this.mentions.add(u.getScreenName());
        }

    }



    public void setText(String text) {
        this.text = text.intern();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }


    @Override
    public String toString() {
        return "Tweet{" + "text=" + text + ", user=" + user + ", hashtags=" + hashtags + ", mentions=" + mentions + '}';
    }
}

