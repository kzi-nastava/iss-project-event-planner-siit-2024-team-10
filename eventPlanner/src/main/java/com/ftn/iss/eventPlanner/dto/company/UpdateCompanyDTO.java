package com.ftn.iss.eventPlanner.dto.company;

import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompanyDTO {
    private int id;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private CreateLocationDTO location;

    public UpdateCompanyDTO() {}
}
