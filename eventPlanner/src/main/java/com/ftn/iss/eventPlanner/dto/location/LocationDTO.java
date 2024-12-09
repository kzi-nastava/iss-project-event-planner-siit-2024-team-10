package com.ftn.iss.eventPlanner.dto.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetLocationDTO {
    private int id;
    private String city;
    private String country;
    private String street;
    private String houseNumber;

    public GetLocationDTO() {}

    public GetLocationDTO(String city, String country, String street, String houseNumber) {
        this.city = city;
        this.country = country;
        this.street = street;
        this.houseNumber = houseNumber;
    }
}
