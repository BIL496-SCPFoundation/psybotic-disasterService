package com.scpfoundation.psybotic.disastercheckservice.Models;

public class MyLocation {
    private String city;
    private String province;

    public MyLocation(String city, String province) {
        this.city = city;
        this.province = province;
    }


    public String locationName() {
        return province+" "+city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
