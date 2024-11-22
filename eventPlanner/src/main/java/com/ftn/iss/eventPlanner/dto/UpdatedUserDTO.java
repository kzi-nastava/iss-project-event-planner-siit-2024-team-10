package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedUserDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private LocationDTO location;
    private UpdatedCompanyDTO company;

    public UpdatedUserDTO() {}
}
