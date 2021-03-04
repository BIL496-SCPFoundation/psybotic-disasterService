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
    private Timestamp date;
    private double latitude;
    private double longitude;


    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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
    @PostMapping(
            value = "/findById", consumes = "application/json", produces = "application/json")
    public Disaster updatePerson(@RequestBody Disaster ds1, HttpServletResponse response) {
        response.setHeader("Location", ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/findPerson/" + ds1.getId()).toUriString());

        return new DisasterService() {
            @Override
            public Disaster saveDisaster(Disaster ds) {
                return null;
            }

            @Override
            public Disaster findDisasterId(String id) {
                return null;
            }
        }.findDisasterId(id);
    }


}
