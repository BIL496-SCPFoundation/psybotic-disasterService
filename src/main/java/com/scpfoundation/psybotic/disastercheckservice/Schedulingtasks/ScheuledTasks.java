package com.scpfoundation.psybotic.disastercheckservice.Schedulingtasks;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;
import com.scpfoundation.psybotic.disastercheckservice.Models.EmergencyContact;
import com.scpfoundation.psybotic.disastercheckservice.Models.Notification;
import com.scpfoundation.psybotic.disastercheckservice.Models.User;
import com.scpfoundation.psybotic.disastercheckservice.Twitter.TwitterAPIController;
import com.scpfoundation.psybotic.disastercheckservice.fcm.FCMService;

import com.scpfoundation.psybotic.disastercheckservice.fcm.model.PushNotificationRequest;
import com.scpfoundation.psybotic.disastercheckservice.fcm.service.PushNotificationService;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import twitter4j.TwitterException;


import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;


//to do update Methods

@Component
public class ScheuledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheuledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    static final long TIMEISANOTIFICAITON =43200000;
    private static MimeMessage mimeMessage;
    private static JavaMailSenderImpl mailSender;

    @Scheduled(fixedRate = 3600000)
    public void reportCurrentTime() throws TwitterException, JsonProcessingException, MessagingException, IOException {
        log.info("The time is now {}", dateFormat.format(new Date()));
        islemleribaslat();
        mailVerileriniOlustur();
        controlReplyTime();
        System.out.println("This Step Is Done");

    }

    private void controlReplyTime() throws MessagingException {
        String getNoReplyedNotification="https://limitless-lake-96203.herokuapp.com/notifications/findByNotificationNoReply?bildiri=false&reply=false";
        String getEmergencyContactThisUser="https://limitless-lake-96203.herokuapp.com/emergencyContacts/findBySuperId?super_id=";
        String pushingNotificationDb= "https://limitless-lake-96203.herokuapp.com/notifications/update";
        RestTemplate rest = new RestTemplate();
        ResponseEntity<Notification[]> responseEntity = rest.getForEntity(getNoReplyedNotification, Notification[].class);
        Notification[] notifications = responseEntity.getBody();
        Date currentDate=new Date();
        Long currentDateTime =currentDate.getTime();
        System.out.println(currentDate+" "+currentDateTime);
        ArrayList<Notification> ntf=new ArrayList<>();
        for (int i = 0; i <notifications.length ; i++) {
            //Time control
            if(notifications[i].getSendingDate()!=null) {
                Long notifitcationdatetime=notifications[i].getSendingDate().getTime()-10800000;
                if ((currentDateTime - notifitcationdatetime) > TIMEISANOTIFICAITON)
                    ntf.add(notifications[i]);
            }
        }
        System.out.println(ntf.size());
        for (int i=0;i<ntf.size();i++)
        {
            Notification a1=ntf.get(i);
            String user_id=a1.getUserId();
            String mesaj=a1.getText();
            String header=a1.getTextHeader();
            Date date=a1.getSendingDate();

            ResponseEntity<EmergencyContact[]> responseEntity1=rest.getForEntity(getEmergencyContactThisUser+user_id,EmergencyContact[].class);
            EmergencyContact[] emergencyperson=responseEntity1.getBody();
            for (int j=0;j<emergencyperson.length;j++)
            {
                EmergencyContact emerge=emergencyperson[j];
                String email=emerge.getEmail();
                String isim=emerge.getFirstName();
                System.out.println(isim+" Adli Emergency Contacta "+email+" Adresine Mail Gonderildi ");
                sendMail(email,"Sevdigin Icin Endiseliyiz...","Merhaba "+isim+"\n"+"\n"+"\n"+" Senin bir yakini oldugunu dusungumuz kullancimiza : \n" +
                        mesaj+"\n"+" " +
                        "Seklinde Mesaj Yolladik 12 Saatten fazla bir sure gectigi icin seni bilgilendirmek istedik. \n"
                        +"Umariz Hersey Yolundandir.\n"+"Lutfen kullanicimiza ulasirsan bizimle iletisime gecmesini soyler misin?"+
                        "\n Saglikli huzurlu gunler dileriz.");


            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            JSONObject notificationJsonObject = new JSONObject();
            notificationJsonObject.put("id",a1.getId());
            notificationJsonObject.put("userId",a1.getUserId());
            notificationJsonObject.put("textHeader",a1.getTextHeader());
            notificationJsonObject.put("text",a1.getText());
            notificationJsonObject.put("status",a1.isStatus());
            notificationJsonObject.put("reply",a1.isReply());
            Date sendingDate=new Date(a1.getSendingDate().getTime());
            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
            System.out.println("Bak ben BUNU bekliyorum" +formatter.format(date));
            notificationJsonObject.put("sendingDate",formatter.format(date));
            notificationJsonObject.put("bildiri",true);
            System.out.println(notificationJsonObject);
            HttpEntity<String> request_notification =
                    new HttpEntity<String>(notificationJsonObject.toString(), headers);
            String notificationResultAsJsonStr =
                    rest.postForObject(pushingNotificationDb, request_notification, String.class);

        }


    }

    private  void sendMail(String setTo,String setSubject,String setText) throws MessagingException {
        System.out.println("Sending Email...");
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
        JavaMailSenderImpl mailSender = ctx.getBean(JavaMailSenderImpl.class);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        //Pass true flag for multipart message
        MimeMessageHelper mailMsg = new MimeMessageHelper(mimeMessage, true);
        mailMsg.setFrom("scpFoundation@gmail.com");
        mailMsg.setTo(setTo);
        mailMsg.setSubject(setSubject);
        mailMsg.setText(setText);
        //FileSystemResource object for Attachment
        FileSystemResource file = new FileSystemResource(new File("/Users/macbookpro/IdeaProjects/psybotic-disasterService/src/main/resources/logo.png"));
        mailMsg.addAttachment("myPic.jpg", file);
        mailSender.send(mimeMessage);
        System.out.println("---Done---");
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void islemleribaslat() throws TwitterException, JsonProcessingException {
        TwitterAPIController twc=new TwitterAPIController();
        ArrayList<Disaster> twits_of_disaster=new ArrayList<>();
        twits_of_disaster=twc.getUserTimeLine("DepremDairesi");
        RestTemplate rest = new RestTemplate();
        String pushingurl = "https://limitless-lake-96203.herokuapp.com/disasters/insert";
        String findingByIdDisaster = "https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
        String pushingNotificationDb= "https://limitless-lake-96203.herokuapp.com//notifications/insert";
        String findNearByuserurl="http://limitless-lake-96203.herokuapp.com/users/findByNearLocation?city=";
        System.out.println(twits_of_disaster.size());
        System.out.println(new Date());
        for (int i=0;i<twits_of_disaster.size();i++)
        {
            String id=twits_of_disaster.get(i).getId();
            findingByIdDisaster="https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
            findingByIdDisaster=findingByIdDisaster+id;
            Disaster ds2 = rest.getForObject(findingByIdDisaster,Disaster.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            System.out.println("Create Disaster"+ds2);
            System.out.println("My twit"+twits_of_disaster.get(i));
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
                newNotification.setId(twits_of_disaster.get(i).getId());
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
                ResponseEntity<User[]> responseEntity = rest.getForEntity(findingnearbyuserurl, User[].class);
                User[] userList = responseEntity.getBody();
                MediaType contentType = responseEntity.getHeaders().getContentType();
                HttpStatus statusCode = responseEntity.getStatusCode();

                for (int j = 0; j < userList.length ; j++) {
                    User users=userList[j];
                    newNotification.setUserId(users.getId());
                    String text="Merhaba"+users.getFirstName()+"\n"
                            +"Seni Cok Merak Ettik"+"Yasadigin Bolgedeye yakin"
                            +twits_of_disaster.get(i).getLocation()+" 'da \n"+twits_of_disaster.get(i).getType()+"yasandi. \n"
                            +"Umarim sen sevdiklerin ve ailen iyidir. \n"+
                            "Lutfen beni bilgilendirir misin,Nasilsin?" ;
                    newNotification.setText(text);
                    JSONObject notificationJsonObject = new JSONObject();
                    notificationJsonObject.put("id",newNotification.getId());
                    notificationJsonObject.put("userId",newNotification.getUserId());
                    notificationJsonObject.put("textHeader",newNotification.getTextHeader());
                    notificationJsonObject.put("text",newNotification.getText());
                    notificationJsonObject.put("status",newNotification.isStatus());
                    notificationJsonObject.put("reply",newNotification.isReply());
                    Date sendingDate=new Date(newNotification.getSendingDate().getTime());
                    notificationJsonObject.put("sendingDate",sendingDate);
                    notificationJsonObject.put("bildiri",false);
                    HttpEntity<String> request_notification =
                            new HttpEntity<String>(notificationJsonObject.toString(), headers);
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
                System.out.println("Disaster Mevcut");

            }

        }

        }


    private static void mailVerileriniOlustur() {
        System.out.println("Mail Verileri Olusturuluyor");
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
        mailSender = ctx.getBean(JavaMailSenderImpl.class);
        mimeMessage = mailSender.createMimeMessage();
    }



}



