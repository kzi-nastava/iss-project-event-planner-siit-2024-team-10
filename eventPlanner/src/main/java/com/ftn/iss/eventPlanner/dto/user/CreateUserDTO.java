package com.ftn.iss.eventPlanner.dto.user;

<<<<<<< HEAD:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/user/CreateUserDTO.java
import com.ftn.iss.eventPlanner.dto.location.LocationDTO;
import com.ftn.iss.eventPlanner.dto.company.CreateCompanyDTO;
=======
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
>>>>>>> develop:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/CreateUserDTO.java
import com.ftn.iss.eventPlanner.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDTO {
    private String email;
    private String password;
    private Role role;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private CreateLocationDTO location;
    private CreateCompanyDTO company;

    public CreateUserDTO() {}

}
