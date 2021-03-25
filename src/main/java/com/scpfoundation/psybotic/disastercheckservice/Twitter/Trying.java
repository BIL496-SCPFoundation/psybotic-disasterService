package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;

import com.scpfoundation.psybotic.disastercheckservice.Models.Notification;
import com.scpfoundation.psybotic.disastercheckservice.Models.User;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trying {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws TwitterException, JsonProcessingException {
        TwitterAPIController twc=new TwitterAPIController();
        ArrayList<Disaster> nereden = new ArrayList<>();
        nereden=twc.getUserTimeLine("DepremDairesi");
        RestTemplate rest = new RestTemplate();
        String pushingurl = "https://limitless-lake-96203.herokuapp.com/disasters/insert";
        String findingById = "https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
        String pushingNotificationDb= "https://limitless-lake-96203.herokuapp.com//notifications/insert";
        for (int i=0;i<nereden.size();i++)
        {
            String id=nereden.get(i).getId();
            findingById=findingById+id;
            Disaster ds2 = rest.getForObject(findingById,Disaster.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if(ds2==null)
            {
                ArrayList<User> users=new ArrayList<>();
                JSONObject disasterJsonObject = new JSONObject();
                disasterJsonObject.put("id", nereden.get(i).getId());
                disasterJsonObject.put("type",nereden.get(i).getType());
                disasterJsonObject.put("location",nereden.get(i).getLocation());
                disasterJsonObject.put("date",nereden.get(i).getDate().toString());
                disasterJsonObject.put("latitude",nereden.get(i).getLatitude());
                disasterJsonObject.put("longitude",nereden.get(i).getLongitude());
                HttpEntity<String> request =
                        new HttpEntity<String>(disasterJsonObject.toString(), headers);
                String personResultAsJsonStr =
                        rest.postForObject(pushingurl, request, String.class);
                JsonNode root = objectMapper.readTree(personResultAsJsonStr);
                System.out.println("Yeni bir felaket eklendi");
                Notification newNotification = new Notification();
                Date date = new Date(System.currentTimeMillis());
                newNotification.setNotificationId(nereden.get(i).getId());
                newNotification.setSendingDate(date);
                newNotification.setReply(false);
                newNotification.setStatus(true);
                newNotification.setTextHeader("Iyi Misin?");
                for (int j = 0; j < users.size() ; j++) {
                        newNotification.setUserId(users.get(i).getId());
                        String text="Merhaba"+users.get(i).getFirstName()
                                +"Seni Cok Merak Ettik"+"Yasadigin Bolgedeye yakin"
                                +nereden.get(i).getLocation()+"'da"+nereden.get(i).getType()+"yasandi."
                                +"Umarim sen sevdiklerin ve ailen iyidir."+
                                "Lutfen beni bilgilendirir misin,Nasilsin?";
                        newNotification.setText(text);
                    JSONObject notificationJsonObject = new JSONObject();
                    notificationJsonObject.put("notificationId",newNotification.getNotificationId());
                    notificationJsonObject.put("userId",newNotification.getUserId());
                    notificationJsonObject.put("textHeader",newNotification.getTextHeader());
                    notificationJsonObject.put("text",newNotification.getText());
                    notificationJsonObject.put("status",newNotification.isStatus());
                    notificationJsonObject.put("reply",newNotification.isReply());
                    notificationJsonObject.put("sendingDate",newNotification.getSendingDate());
                    HttpEntity<String> request_notification =
                            new HttpEntity<String>(disasterJsonObject.toString(), headers);
                    String notificationResultAsJsonStr =
                            rest.postForObject(pushingNotificationDb, request_notification, String.class);
                    JsonNode root_notificaiton = objectMapper.readTree(notificationResultAsJsonStr);

                }
            }
            else
            {
                System.out.println("Burda OLmasi Beklemir");
                break;
            }

        }
    }
}
