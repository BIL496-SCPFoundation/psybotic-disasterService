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
    public static String[] citysInTurkey={"Adana", "Adıyaman",
            "Afyonkarahisar", "Ağrı", "Aksaray", "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin", "Aydın",
            "Balıkesir", "Bartın", "Batman", "Bayburt", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa",
            "Çanakkale", "Çankırı", "Çorum",
            "Denizli", "Diyarbakır", "Düzce",
            "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir",
            "Gaziantep", "Giresun", "Gümüşhane",
            "Hakkâri", "Hatay",
            "Iğdır", "Isparta", "İstanbul", "İzmir",
            "Kahramanmaraş", "Karabük", "Karaman", "Kars", "Kastamonu", "Kayseri", "Kilis", "Kırıkkale", "Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya",
            "Malatya", "Manisa", "Mardin", "Mersin", "Muğla", "Muş",
            "Nevşehir", "Niğde",
            "Ordu", "Osmaniye",
            "Rize",
            "Sakarya", "Samsun", "Şanlıurfa", "Siirt", "Sinop", "Sivas", "Şırnak",
            "Tekirdağ", "Tokat", "Trabzon", "Tunceli",
            "Uşak",
            "Van",
            "Yalova", "Yozgat",
            "Zonguldak"};

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
                boolean tarihteBugunMu=tarihteBugunTwiti(hashTags);
                if(!tarihteBugunMu) {
                    for (int a = 0; a < hashTags.size(); a++) {
                        if (hashTags.get(a).equals("Deprem")) {
                            System.out.println(tw.getText());
                            System.out.println(tw.getTime());
                            Disaster ds1 = new Disaster();
                            ds1.setDate(tw.getTime());
                            ds1.setId(tw.getId());
                            ds1.setType("Deprem");
                            String yer = tw.getText().substring(tw.getText().indexOf("Yer"), tw.getText().indexOf("Tarih-Saat"));
                            String[] yerler = yer.split(" ");
                            String istenen = yerler[3].substring(1) + "-" + yerler[2];
                            ds1.setLocation(istenen);
                            String enlem = tw.getText().substring(tw.getText().indexOf("Enlem"), tw.getText().indexOf("Boylam"));
                            String[] istenen_enlem = enlem.split(" ");
                            Double enlem_cinsi = Double.parseDouble(istenen_enlem[2]);
                            ds1.setLatitude(enlem_cinsi);
                            String boylam = tw.getText().substring(tw.getText().indexOf("Boylam"), tw.getText().indexOf("Derinlik"));
                            String[] istenen_boylam = boylam.split(" ");
                            Double boylam_cinsi = Double.parseDouble(istenen_boylam[2]);
                            ds1.setLongitude(boylam_cinsi);
                            System.out.println(ds1.toString());
                            disaster.add(ds1);
                            System.out.println("-----------------********************------------------");
                        }
                    }
                }
            }
            System.exit(0);
            return disaster;
        }
        catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
            return null;
        }
    }

    private boolean tarihteBugunTwiti(List<String> hashTags) {
        boolean yes=false;
        for (int i = 0; i < hashTags.size(); i++) {
            if(hashTags.get(i).equals("TarihteBugün"))
            {
                yes=true;
                break;
            }
        }
        return yes;
    }

}
