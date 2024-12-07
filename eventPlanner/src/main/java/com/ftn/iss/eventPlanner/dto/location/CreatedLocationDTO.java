package com.ftn.iss.eventPlanner.dto.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedLocationDTO {
    private int id;
    private String city;
    private String country;
    private String street;
    private String houseNumber;

    public CreatedLocationDTO() {}

    public CreatedLocationDTO(String city, String country, String street, String houseNumber) {
        this.city = city;
        this.country = country;
        this.street = street;
        this.houseNumber = houseNumber;
    }
}
