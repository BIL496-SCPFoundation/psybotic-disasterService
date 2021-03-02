package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.*;

public class TwitterAPIController {
    private List<Tweet> listTweets;
    private int count = 0;
    private ConfigurationBuilder cb;

    public TwitterAPIController() {
        cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("QJECcZsXyTNMnKztqA4Jbrl4i")
                .setOAuthConsumerSecret("94nfaWzrs7KeyNqMbhZZvl3XKKf09YBqxGGT8OndLwF61eLSyj")
                .setOAuthAccessToken("428225393-dn7FbH58JZYQQ7wBxm0hRjzcUs2OjP04OycKgQod")
                .setOAuthAccessTokenSecret("FqAIdQdMqDdxZJqLW7e8OWSEFs1COq6gDEZVra7aUTbfZ");
    }

    public List<Tweet> getTweetsFromSearchAPI(String string) throws TwitterException {
        listTweets = new ArrayList();

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Query query = new Query(string);
        query.lang("en");
        query.count(1000);
        QueryResult result;
        int nbTweets = 0;
        int nbPages = 0;
        boolean finished = false;
        Tweet tweet;

        Set<Long> ids = new HashSet();
        long lowestStatusId = Long.MAX_VALUE;

        while (!finished) {
            if (nbPages++ > 8) {
                return listTweets;
            }
            try {
                result = twitter.search(query);
            } catch (Exception e) {
                return listTweets;
            }
            for (Status status : result.getTweets()) {
                if (!ids.contains(status.getId())) {
                    tweet = new Tweet(status);
                    tweet.setUser("@"+status.getUser().getScreenName());
                    listTweets.add(new Tweet(status));
                    ids.add(status.getId());
                }
                // Capture the lowest (earliest) Status id
                lowestStatusId = Math.min(status.getId(), lowestStatusId);
                if (nbTweets++ > 5000) {
                    finished = true;
                }
            }
            // Subtracting one here because 'max_id' is inclusive
            query.setMaxId(lowestStatusId - 1);

        }
        return listTweets;
    }
    public List<Status> getUserTimeLine(String userName)
    {
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        List<Status> statuses;
        try {

            String user=userName;
            statuses=twitter.getUserTimeline(user);
            System.out.println("Showing @" + user + "'s user timeline.");
            Tweet tw;
            for (Status status : statuses) {
                tw=new Tweet(status);
                List<String> hashTags = tw.getHashtags();
                for (int a=0;a<hashTags.size();a++) {
                    if (hashTags.get(a).equals("Deprem")) {
                        System.out.println(tw.getText());
                        System.out.println(tw.getCreating());
                        System.out.println();
                        System.out.println();
                        // Assuming all hashtags are unique...
                        continue;
                    }
                }
            }
            return statuses;
        }
        catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
            return null;
        }
    }

}
