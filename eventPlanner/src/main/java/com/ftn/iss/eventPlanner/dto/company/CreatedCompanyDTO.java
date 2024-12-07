package com.ftn.iss.eventPlanner.dto.company;

<<<<<<< HEAD:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/company/CreatedCompanyDTO.java
import com.ftn.iss.eventPlanner.dto.location.LocationDTO;
=======
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
>>>>>>> develop:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/CreatedCompanyDTO.java
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatedCompanyDTO {
    private int id;
    private String email;
    private String name;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private CreatedLocationDTO location;

    public CreatedCompanyDTO() {}

    public CreatedCompanyDTO(int id, String email, String name, String phoneNumber, String description, List<String> photos, CreatedLocationDTO location) {
        this.id=id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
