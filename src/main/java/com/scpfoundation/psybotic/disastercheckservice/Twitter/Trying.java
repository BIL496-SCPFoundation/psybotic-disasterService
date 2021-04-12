package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;

import com.scpfoundation.psybotic.disastercheckservice.Models.EmergencyContact;
import com.scpfoundation.psybotic.disastercheckservice.Models.Notification;
import com.scpfoundation.psybotic.disastercheckservice.Models.User;
import com.scpfoundation.psybotic.disastercheckservice.fcm.FCMService;
import com.scpfoundation.psybotic.disastercheckservice.fcm.model.PushNotificationRequest;
import com.scpfoundation.psybotic.disastercheckservice.fcm.service.PushNotificationService;
import com.sun.tools.corba.se.idl.constExpr.Not;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import twitter4j.TwitterException;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trying {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static final long TIMEISANOTIFICAITON =43200000;
    public static void main(String[] args) throws TwitterException, JsonProcessingException {

        controlReplyTime();

    }
    private static void controlReplyTime() {
        String getNoReplyedNotification="https://limitless-lake-96203.herokuapp.com/notifications/findByNotificationNoReply?bildiri=false&reply=false";
        String getEmergencyContactThisUser="https://limitless-lake-96203.herokuapp.com/emergencyContacts/findBySuperId?super_id=";
        RestTemplate rest = new RestTemplate();
        ResponseEntity<Notification[]> responseEntity = rest.getForEntity(getNoReplyedNotification, Notification[].class);
        Notification[] notifications = responseEntity.getBody();
        Date currentDate=new Date();
        Long currentDateTime =currentDate.getTime();
        System.out.println(currentDate+" "+currentDateTime);
        ArrayList<Notification> ntf=new ArrayList<>();
        for (int i = 0; i <notifications.length ; i++) {
            //Time control
            Long notifitcationdatetime=notifications[i].getSendingDate().getTime()-10800000;
            if((currentDateTime-notifitcationdatetime)>TIMEISANOTIFICAITON)
                ntf.add(notifications[i]);
        }
        for (int i=0;i<ntf.size();i++)
        {
            Notification a1=ntf.get(i);
            String user_id=a1.getUserId();
            String mesaj=a1.getText();
            Date date=a1.getSendingDate();
            ResponseEntity<EmergencyContact[]> responseEntity1=rest.getForEntity(getEmergencyContactThisUser+user_id,EmergencyContact[].class);
            EmergencyContact[] emergencyperson=responseEntity1.getBody();
            for (int j=0;j<emergencyperson.length;j++)
            {
                EmergencyContact emerge=emergencyperson[j];
                String email=emerge.getEmail();
                String isim=emerge.getFirstName();
                System.out.println(isim+" Adli Emergency Contacta "+email+" Adresine Mail Gonderildi ");
            }

        }


    }


        /*
        TwitterAPIController twc=new TwitterAPIController();
        ArrayList<Disaster> twits_of_disaster;
        twits_of_disaster=twc.getUserTimeLine("DepremDairesi");
        RestTemplate rest = new RestTemplate();
        String pushingurl = "https://limitless-lake-96203.herokuapp.com/disasters/insert";
        String findingByIdDisaster = "https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
        String pushingNotificationDb= "https://limitless-lake-96203.herokuapp.com//notifications/insert";
        String findNearByuserurl="http://limitless-lake-96203.herokuapp.com/users/findByNearLocation?city=";

        for (int i=0;i<twits_of_disaster.size();i++)
        {
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
                    System.out.println(twits_of_disaster.get(i).getDate());
                    disasterJsonObject.put("date",twits_of_disaster.get(i).getDate().getTime()+10800000);
                    disasterJsonObject.put("latitude",twits_of_disaster.get(i).getLatitude());
                    disasterJsonObject.put("longitude",twits_of_disaster.get(i).getLongitude());
                   //disasterJsonObject.put("magnitude",twits_of_disaster.get(i).getMagnitude());
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
                    String latitude="latitude="+twits_of_disaster.get(i).getLatitude()+"&";
                    String longitude="longitude="+twits_of_disaster.get(i).getLatitude();
                    String findingnearbyuserurl=findNearByuserurl+city+latitude+longitude;
                    System.out.println(findingnearbyuserurl);
                    System.out.println("Olusturtuldum");
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

                    System.out.println("Sistem Guncel");
                    break;
                }

        }
    }

         */
}
