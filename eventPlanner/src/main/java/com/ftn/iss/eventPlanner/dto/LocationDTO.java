package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private String city;
    private String country;
    private String street;
    private String houseNumber;

    public LocationDTO() {}

    public LocationDTO(String city, String country, String street, String houseNumber) {
        this.city = city;
        this.country = country;
        this.street = street;
        this.houseNumber = houseNumber;
    }
}
