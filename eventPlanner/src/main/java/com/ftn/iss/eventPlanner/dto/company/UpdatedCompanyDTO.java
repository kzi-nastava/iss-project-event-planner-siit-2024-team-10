package com.ftn.iss.eventPlanner.dto.company;

<<<<<<< HEAD:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/company/UpdatedCompanyDTO.java
import com.ftn.iss.eventPlanner.dto.location.LocationDTO;
=======
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
>>>>>>> develop:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/UpdatedCompanyDTO.java
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatedCompanyDTO {
    private int id;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private CreatedLocationDTO location;

    public UpdatedCompanyDTO() {}

    public UpdatedCompanyDTO(int id, String phoneNumber, String description, List<String> photos, CreatedLocationDTO location) {
        this.id=id;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.photos = photos;
        this.location = location;
    }
}
