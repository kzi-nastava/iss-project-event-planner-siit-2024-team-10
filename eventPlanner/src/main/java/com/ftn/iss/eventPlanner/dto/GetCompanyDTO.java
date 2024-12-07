package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetCompanyDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private GetLocationDTO location;

    public GetCompanyDTO() {}

    public GetCompanyDTO(String email, String name, String phoneNumber, String description, List<String> photos, GetLocationDTO location) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
