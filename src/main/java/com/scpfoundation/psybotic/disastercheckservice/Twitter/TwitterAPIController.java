package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;
import com.scpfoundation.psybotic.disastercheckservice.Models.MyLocation;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.*;

public class TwitterAPIController {
    private List<Tweet> listTweets;
    private int count = 0;
    private ConfigurationBuilder cb;

    public static ArrayList<Disaster> disaster=new ArrayList<>();
    public static String[] citysInTurkey={"Adana", "Adiyaman",
            "Afyonkarahisar", "Agri", "Aksaray", "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin", "Aydin",
            "Balikesir", "Bartin", "Batman", "Bayburt", "Bilecik", "Bingol", "Bitlis", "Bolu", "Burdur", "Bursa",
            "Canakkale", "Cankiri", "Corum",
            "Denizli", "Diyarbakir", "Duzce",
            "Edirne", "Elazig", "Erzincan", "Erzurum", "Eskisehir",
            "Gaziantep", "Giresun", "Gumushane",
            "Hakkari", "Hatay",
            "Igdir", "Isparta", "Istanbul", "Izmir",
            "Kahramanmaras", "Karabuk", "Karaman", "Kars", "Kastamonu", "Kayseri", "Kilis", "Kirikkale", "Kirklareli", "Kirsehir", "Kocaeli", "Konya", "Kutahya",
            "Malatya", "Manisa", "Mardin", "Mersin", "Mugla", "Mus",
            "Nevsehir", "Nigde",
            "Ordu", "Osmaniye",
            "Rize",
            "Sakarya", "Samsun", "Sanliurfa", "Siirt", "Sinop", "Sivas", "Sirnak",
            "Tekirdag", "Tokat", "Trabzon", "Tunceli",
            "Usak",
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
                            //System.out.println(tw.getText());
                            //System.out.println(tw.getTime());
                            Disaster ds1 = new Disaster();
                            ds1.setDate(tw.getTime());
                            ds1.setId(tw.getId());
                            ds1.setType("Deprem");
                            String buyuk=tw.getText().substring(tw.getText().indexOf("Büyüklük : "),tw.getText().indexOf("Yer"));
                            String[] buyukluk_row=buyuk.split(" ");
                            Double power=Double.parseDouble(buyukluk_row[2]);
                            ds1.setMagnitude(power);
                            String yer = tw.getText().substring(tw.getText().indexOf("Yer"), tw.getText().indexOf("Tarih-Saat"));
                            yer=yer.substring(0,yer.lastIndexOf(")")+1);
                            String[] citys=yer.split(" ");
                            MyLocation location_full_citys_province=ilibul(citys,yer);
                            String str=location_full_citys_province.locationName();
                            ds1.setLocation(str);
                            String enlem = tw.getText().substring(tw.getText().indexOf("Enlem"), tw.getText().indexOf("Boylam"));
                            String[] istenen_enlem = enlem.split(" ");
                            Double enlem_cinsi = Double.parseDouble(istenen_enlem[2]);
                            ds1.setLatitude(enlem_cinsi);
                            String boylam = tw.getText().substring(tw.getText().indexOf("Boylam"), tw.getText().indexOf("Derinlik"));
                            String[] istenen_boylam = boylam.split(" ");
                            Double boylam_cinsi = Double.parseDouble(istenen_boylam[2]);
                            ds1.setLongitude(boylam_cinsi);
                            //System.out.println(ds1.toString());
                            disaster.add(ds1);
                            //System.out.println("-----------------********************------------------");
                        }
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

    private MyLocation ilibul(String[] yer,String ifade) {
        int parantezliifade=countparantez(ifade);
        MyLocation l1 = null;
        int beforeparentez=0;
        int ilifadesi=0;
        if(parantezliifade>1)
        {
            int temp=0;
            for (int i = 0; i <yer.length; i++) {

                    if (yer[i].indexOf("(") >= 0) {
                        temp++;
                        if(temp==parantezliifade) {

                            beforeparentez = i - 1;
                            ilifadesi = i;
                            break;
                        }
                    }

            }
            l1=new MyLocation(yer[ilifadesi],yer[beforeparentez]);
        }
        else
        {
            for (int i = 0; i <yer.length; i++) {
                if(yer[i].indexOf("(")>=0)
                {
                    beforeparentez=i-1;
                    ilifadesi=i;
                    break;
                }
            }
            l1=new MyLocation(yer[ilifadesi],yer[beforeparentez]);
        }
        return l1;
    }

    private int countparantez(String ifade) {
        int sayisi=0;
        for (int i = 0; i <ifade.length(); i++) {
            if(ifade.charAt(i)==40)
                sayisi++;
        }

        return sayisi;
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
