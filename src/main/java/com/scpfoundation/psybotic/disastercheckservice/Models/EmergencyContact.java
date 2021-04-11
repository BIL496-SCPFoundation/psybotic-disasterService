package com.scpfoundation.psybotic.disastercheckservice.Models;

import java.util.Objects;

public class EmergencyContact {
    private String id;
    private String phone;
    private String type;
    private String email;
    private String firstName;
    private String lastName;
    private String superId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuperId() {
        return superId;
    }

    public void setSuperId(String superId) {
        this.superId = superId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmergencyContact)) return false;
        EmergencyContact that = (EmergencyContact) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(getPhone(), that.getPhone()) &&
                Objects.equals(getType(), that.getType()) &&
                Objects.equals(getEmail(), that.getEmail()) &&
                Objects.equals(getFirstName(), that.getFirstName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getPhone(), getType(), getEmail(), getFirstName());
    }

}
