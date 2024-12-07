package com.ftn.iss.eventPlanner.dto.user;

import com.ftn.iss.eventPlanner.dto.company.UpdateCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.LocationDTO;
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
