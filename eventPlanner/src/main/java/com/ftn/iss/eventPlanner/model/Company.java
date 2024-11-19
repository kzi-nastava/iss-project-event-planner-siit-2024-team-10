package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
}
