package com.scpfoundation.psybotic.disastercheckservice.Schedulingtasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;
import com.scpfoundation.psybotic.disastercheckservice.Twitter.TwitterAPIController;
import com.scpfoundation.psybotic.disastercheckservice.fcm.PushNotificationController;
import com.scpfoundation.psybotic.disastercheckservice.fcm.model.PushNotificationRequest;
import com.scpfoundation.psybotic.disastercheckservice.fcm.service.PushNotificationService;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import twitter4j.TwitterException;

@Component
public class ScheuledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheuledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 3600000)
    public void reportCurrentTime() throws TwitterException, JsonProcessingException {
        log.info("The time is now {}", dateFormat.format(new Date()));
        islemleribaslat();
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void islemleribaslat() throws TwitterException, JsonProcessingException {
        TwitterAPIController twc=new TwitterAPIController();
        ArrayList<Disaster> nereden = new ArrayList<>();
        nereden=twc.getUserTimeLine("DepremDairesi");
        RestTemplate rest = new RestTemplate();
        String pushingurl = "https://limitless-lake-96203.herokuapp.com/disasters/insert";
        String findingById = "https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
        for (int i=0;i<nereden.size();i++)
        {
            PushNotificationController pushNotificationController;
            String id=nereden.get(i).getId();
            findingById=findingById+id;
            Disaster ds2 = rest.getForObject(findingById,Disaster.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if(ds2==null)
            {
                Disaster d = new Disaster();
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
                String token=nereden.get(i).toString();
                PushNotificationRequest req=new PushNotificationRequest();
                PushNotificationService s1 = null;
                req.setTitle("GECMIS OLSUN");
                req.setMessage("Yeni Bir Deprem Yasandi Iyi Misin?");
                req.setToken(token);
                s1.sendPushNotificationToToken(req);

            }
            else
            {
                System.out.println("Sistem Guncel");
                break;
            }

        }

        }



    }