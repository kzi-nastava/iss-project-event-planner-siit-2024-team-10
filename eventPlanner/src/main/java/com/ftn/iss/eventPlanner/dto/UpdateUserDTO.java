package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private LocationDTO location;
    private UpdateCompanyDTO company;

    public UpdateUserDTO() {}
}
