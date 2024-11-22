package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDTO {
    private int id;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private LocationDTO location;
    private GetCompanyDTO company;

    public GetUserDTO(){}
}
