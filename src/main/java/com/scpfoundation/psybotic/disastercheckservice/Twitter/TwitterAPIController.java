package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.*;

public class TwitterAPIController {
    private List<Tweet> listTweets;
    private int count = 0;
    private ConfigurationBuilder cb;

    public static ArrayList<Disaster> disaster=new ArrayList<>();


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
    public ArrayList<Disaster> getUserTimeLine(String userName)
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
                        Disaster ds1=new Disaster();
                        ds1.setDate(tw.getTime());
                        ds1.setId(tw.getId());
                        ds1.setType("Deprem");
                        String yer =tw.getText().substring(tw.getText().indexOf("Yer"),tw.getText().indexOf("Tarih-Saat"));
                        String[] yerler=yer.split(" ");
                        String istenen=yerler[3].substring(1)+"-"+yerler[2];
                        ds1.setLocation(istenen);
                        String enlem=tw.getText().substring(tw.getText().indexOf("Enlem"),tw.getText().indexOf("Boylam"));
                        String[] istenen_enlem=enlem.split(" ");
                        Double enlem_cinsi=Double.parseDouble(istenen_enlem[2]);
                        ds1.setLatitude(enlem_cinsi);
                        String boylam=tw.getText().substring(tw.getText().indexOf("Boylam"),tw.getText().indexOf("Derinlik"));
                        String[] istenen_boylam=boylam.split(" ");
                        Double boylam_cinsi=Double.parseDouble(istenen_boylam[2]);
                        ds1.setLongitude(boylam_cinsi);
                        disaster.add(ds1);
                        System.out.println(ds1.toString());
                    }
                }
            }
            return disaster;
        }
        catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
            return null;
        }
    }

}
