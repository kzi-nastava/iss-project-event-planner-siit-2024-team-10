package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
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
    private CreatedLocationDTO location;

    public UpdatedCompanyDTO() {}

    public UpdatedCompanyDTO(int id, String phoneNumber, String description, List<String> photos, CreatedLocationDTO location) {
        this.id=id;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
