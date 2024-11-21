package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private LocationDTO location;

    public CompanyDTO() {}

    public CompanyDTO(String email, String name, String phoneNumber, String description, List<String> photos, LocationDTO location) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
