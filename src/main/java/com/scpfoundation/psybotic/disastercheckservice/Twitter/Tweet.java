package com.scpfoundation.psybotic.disastercheckservice.Twitter;


import com.sun.tools.javac.util.Pair;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

import java.sql.Timestamp;
import java.util.*;

public class Tweet {

    private String text;
    private String user;
    private String language;
    private Timestamp time;
    private List<String> hashtags;
    private List<String> mentions;
    private Date creating;




    public Tweet(Status status) {
        this.text = status.getText();
        this.user = status.getUser().getScreenName();
        this.language = status.getUser().getLang();
        this.hashtags = new ArrayList();
        this.creating=status.getCreatedAt();
        for (HashtagEntity h : status.getHashtagEntities()) {
            this.hashtags.add(h.getText());
        }
        this.mentions = new ArrayList();
        for (UserMentionEntity u : status.getUserMentionEntities()) {
            this.mentions.add(u.getScreenName());
        }

    }

    public Date getCreating() {
        return creating;
    }

    public void setCreating(Date creating) {
        this.creating = creating;
    }
    public String getText() {
        return text;
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

