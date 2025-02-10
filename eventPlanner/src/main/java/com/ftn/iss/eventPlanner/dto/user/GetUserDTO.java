package com.ftn.iss.eventPlanner.dto.user;


import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDTO {
    private int accountId;
    private int userId;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private GetLocationDTO location;
    private GetCompanyDTO company;

    public GetUserDTO(){}
}
