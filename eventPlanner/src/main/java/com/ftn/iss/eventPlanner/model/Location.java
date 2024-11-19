package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
    private int id;
    private String city;
    private String country;
    private String street;
    private String houseNumber;

    public Location() {
    }
}
