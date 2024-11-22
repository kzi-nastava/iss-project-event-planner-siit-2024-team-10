package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatedCompanyDTO {
    private int id;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private LocationDTO location;

    public UpdatedCompanyDTO() {}

    public UpdatedCompanyDTO(int id, String phoneNumber, String description, List<String> photos, LocationDTO location) {
        this.id=id;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
