package com.scpfoundation.psybotic.disastercheckservice.Twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scpfoundation.psybotic.disastercheckservice.Models.Disaster;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Trying {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws TwitterException, JsonProcessingException {
        TwitterAPIController twc=new TwitterAPIController();
        ArrayList<Disaster> nereden = new ArrayList<>();
        nereden=twc.getUserTimeLine("DepremDairesi");
        RestTemplate rest = new RestTemplate();
        String pushingurl = "https://limitless-lake-96203.herokuapp.com/disasters/insert";
        String pushingNotificationDb= "https://limitless-lake-96203.herokuapp.com//notifications/insert";
        String findingById = "https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
        for (int i=0;i<nereden.size();i++)
        {
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
            }
            else
            {
                System.out.println("Burda OLmasi Beklemir");
                break;
            }

        }
    }
}
