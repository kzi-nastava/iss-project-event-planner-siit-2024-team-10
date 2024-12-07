package com.ftn.iss.eventPlanner.dto.company;

import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
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
    private CreatedLocationDTO location;

    public CreatedCompanyDTO() {}

    public CreatedCompanyDTO(int id, String email, String name, String phoneNumber, String description, List<String> photos, CreatedLocationDTO location) {
        this.id=id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
