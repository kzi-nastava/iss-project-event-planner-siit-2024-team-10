package com.ftn.iss.eventPlanner.dto.user;

<<<<<<< HEAD:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/user/GetUserDTO.java
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.LocationDTO;
=======
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
>>>>>>> develop:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/GetUserDTO.java
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
    private GetLocationDTO location;
    private GetCompanyDTO company;

    public GetUserDTO(){}
}
