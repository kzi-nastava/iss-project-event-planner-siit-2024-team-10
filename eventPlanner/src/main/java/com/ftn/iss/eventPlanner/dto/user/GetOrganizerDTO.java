package com.ftn.iss.eventPlanner.dto.user;

import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetOrganizerDTO {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private GetLocationDTO location;
}
