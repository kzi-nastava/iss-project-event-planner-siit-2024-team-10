package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatedCompanyDTO {
    private int id;
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private LocationDTO location;

    public CreatedCompanyDTO() {}

    public CreatedCompanyDTO(int id, String email, String name, String phoneNumber, String description, List<String> photos, LocationDTO location) {
        this.id=id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
