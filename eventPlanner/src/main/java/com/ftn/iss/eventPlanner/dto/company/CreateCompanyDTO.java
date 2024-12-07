package com.ftn.iss.eventPlanner.dto.company;

<<<<<<< HEAD:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/company/CreateCompanyDTO.java
import com.ftn.iss.eventPlanner.dto.location.LocationDTO;
=======
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
>>>>>>> develop:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/CreateCompanyDTO.java
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateCompanyDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private CreateLocationDTO location;

    public CreateCompanyDTO() {}

    public CreateCompanyDTO(String email, String name, String phoneNumber, String description, List<String> photos, CreateLocationDTO location) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
