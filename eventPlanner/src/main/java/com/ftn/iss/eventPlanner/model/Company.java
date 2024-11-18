package com.ftn.iss.eventPlanner.model;

import java.util.List;

public class Company {
    private int id;
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private Location location;

    public Company() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
