package com.ftn.iss.eventPlanner.dto.user;

import com.ftn.iss.eventPlanner.dto.company.CreatedCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import com.ftn.iss.eventPlanner.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedUserDTO {
    private int id;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private CreatedLocationDTO location;
    private CreatedCompanyDTO company;
}
