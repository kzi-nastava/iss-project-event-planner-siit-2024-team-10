package com.ftn.iss.eventPlanner.dto.user;

import com.ftn.iss.eventPlanner.dto.company.UpdatedCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedUserDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private CreatedLocationDTO location;

    public UpdatedUserDTO() {}
}
