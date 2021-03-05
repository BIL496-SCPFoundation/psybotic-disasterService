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
        String url = "https://limitless-lake-96203.herokuapp.com/disasters/insert";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Disaster d = new Disaster();
        JSONObject disasterJsonObject = new JSONObject();
        disasterJsonObject.put("id", "deneme");
        HttpEntity<String> request =
                new HttpEntity<String>(disasterJsonObject.toString(), headers);
        String personResultAsJsonStr =
                rest.postForObject(url, request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr);
        System.out.println(root);
        //GeneralResponse gr = rest.postForObject(url, request, GeneralResponse.class);

        String url2 = "https://limitless-lake-96203.herokuapp.com/disasters/findById?id=";
        Integer empId= 3;
        url2=url2+empId;
        RestTemplate restTemplate = new RestTemplate();
        Disaster ds2 = restTemplate.getForObject(url2,Disaster.class);
    }
}
