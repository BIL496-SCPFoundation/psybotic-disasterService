package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.List;

public class Trying {
    public static void main(String[] args) throws TwitterException {
        TwitterAPIController twc=new TwitterAPIController();
        List<Tweet> nereden = twc.getTweetsFromSearchAPI("DepremDairesi");
        TwitterAPIController twc2=new TwitterAPIController();
        List<Status> status = twc2.getUserTimeLine("@DepremDairesi");

    }
}
