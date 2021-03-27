package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;

import com.scpfoundation.psybotic.disastercheckservice.Models.Notification;
import com.scpfoundation.psybotic.disastercheckservice.Models.User;
import com.scpfoundation.psybotic.disastercheckservice.fcm.FCMService;
import com.scpfoundation.psybotic.disastercheckservice.fcm.model.PushNotificationRequest;
import com.scpfoundation.psybotic.disastercheckservice.fcm.service.PushNotificationService;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import twitter4j.TwitterException;


import java.util.ArrayList;
import java.util.Date;

public class Trying {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws TwitterException, JsonProcessingException {
        TwitterAPIController twc=new TwitterAPIController();
        ArrayList<Disaster> twits_of_disaster;
        twits_of_disaster=twc.getUserTimeLine("DepremDairesi");
        RestTemplate rest = new RestTemplate();
        String pushingurl = "https://limitless-lake-96203.herokuapp.com/disasters/insert";
        String findingByIdDisaster = "https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
        String pushingNotificationDb= "https://limitless-lake-96203.herokuapp.com//notifications/insert";
        String findNearByuserurl="http://limitless-lake-96203.herokuapp.com/users/findByNearLocation?city=";
        System.out.println(twits_of_disaster.size());
        for (int i=0;i<twits_of_disaster.size();i++)
        {
            System.out.println("---------"+twits_of_disaster.get(i).getLocation()+twits_of_disaster.get(i).getLocation());
            System.out.println(twits_of_disaster.get(i).getLocation()+twits_of_disaster.get(i).getId());
            String id=twits_of_disaster.get(i).getId();
            findingByIdDisaster=findingByIdDisaster+id;
            Disaster ds2 = rest.getForObject(findingByIdDisaster,Disaster.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if(ds2==null)
                {
                    JSONObject disasterJsonObject = new JSONObject();
                    disasterJsonObject.put("id", twits_of_disaster.get(i).getId());
                    disasterJsonObject.put("type",twits_of_disaster.get(i).getType());
                    disasterJsonObject.put("location",twits_of_disaster.get(i).getLocation());
                    disasterJsonObject.put("date",twits_of_disaster.get(i).getDate().toString());
                    disasterJsonObject.put("latitude",twits_of_disaster.get(i).getLatitude());
                    disasterJsonObject.put("longitude",twits_of_disaster.get(i).getLongitude());
                    HttpEntity<String> request =
                            new HttpEntity<String>(disasterJsonObject.toString(), headers);
                    String personResultAsJsonStr =
                            rest.postForObject(pushingurl, request, String.class);
                    JsonNode root = objectMapper.readTree(personResultAsJsonStr);
                    System.out.println("Yeni bir felaket eklendi");
                    Notification newNotification = new Notification();
                    Date date = new Date(System.currentTimeMillis());
                    newNotification.setNotificationId(twits_of_disaster.get(i).getId());
                    newNotification.setSendingDate(date);
                    newNotification.setReply(false);
                    newNotification.setStatus(true);
                    newNotification.setTextHeader("Iyi Misin?");
                    String city="\""+twits_of_disaster.get(i).getLocation()+"\""+"&";
                    String latitude="latitide="+twits_of_disaster.get(i).getLatitude()+"&";
                    String longitude="longitude="+twits_of_disaster.get(i).getLatitude();
                    String findingnearbyuserurl=findNearByuserurl+city+latitude+longitude;
                    ResponseEntity<Object[]> responseEntity = rest.getForEntity(findingnearbyuserurl, Object[].class);
                    Object[] objects = responseEntity.getBody();
                    MediaType contentType = responseEntity.getHeaders().getContentType();
                    HttpStatus statusCode = responseEntity.getStatusCode();

                        for (int j = 0; j < objects.length ; j++) {
                            User users=(User)objects[j];
                                newNotification.setUserId(users.getId());
                                String text="Merhaba"+users.getFirstName()
                                        +"Seni Cok Merak Ettik"+"Yasadigin Bolgedeye yakin"
                                        +twits_of_disaster.get(i).getLocation()+"'da"+twits_of_disaster.get(i).getType()+"yasandi."
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
                            String token=users.getDeviceToken();
                            if(token!=null) {
                                PushNotificationRequest req = new PushNotificationRequest();
                                PushNotificationService s1 = new PushNotificationService(new FCMService());
                                req.setTitle(newNotification.getTextHeader());
                                req.setMessage(newNotification.getText());
                                req.setToken(token);
                                s1.sendPushNotificationToToken(req);
                            }
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
