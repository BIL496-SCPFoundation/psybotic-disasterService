package com.scpfoundation.psybotic.disastercheckservice.Models;


import com.scpfoundation.psybotic.disastercheckservice.Disaster_Service.DisasterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

public class Disaster{
    private String id;
    private String type;
    private String location;
    private Date date;
    private double latitude;
    private double longitude;
    private double magnitude;



    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disaster)) return false;
        Disaster disaster = (Disaster) o;
        return Objects.equals(id, disaster.id) &&
                Objects.equals(getType(), disaster.getType()) &&
                Objects.equals(getLocation(), disaster.getLocation()) &&
                Objects.equals(getLatitude(),disaster.getLatitude()) &&
                Objects.equals(getLongitude(),disaster.getLongitude()) &&
                Objects.equals(getDate(), disaster.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getType(), getLocation(), getDate());
    }

    @PostMapping(
            value = "/insert", consumes = "application/json", produces = "application/json")
    public Disaster createDisaster(@RequestBody Disaster ds) {
        return new DisasterService() {
            @Override
            public Disaster saveDisaster(Disaster ds) {
                return null;
            }

            @Override
            public Disaster findDisasterId(String id) {
                return null;
            }
        }.saveDisaster(ds);
    }

    @Override
    public String toString() {
        return "Disaster{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", magnitude=" + magnitude +
                '}';
    }
}
