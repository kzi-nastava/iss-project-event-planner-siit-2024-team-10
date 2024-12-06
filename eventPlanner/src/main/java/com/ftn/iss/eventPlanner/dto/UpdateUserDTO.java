package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private CreateLocationDTO location;
    private UpdateCompanyDTO company;

    public UpdateUserDTO() {}
}
