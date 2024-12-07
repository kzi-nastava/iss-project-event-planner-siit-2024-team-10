package com.ftn.iss.eventPlanner.dto.company;

<<<<<<< HEAD:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/company/UpdateCompanyDTO.java
import com.ftn.iss.eventPlanner.dto.location.LocationDTO;
=======
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
>>>>>>> develop:eventPlanner/src/main/java/com/ftn/iss/eventPlanner/dto/UpdateCompanyDTO.java
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompanyDTO {
    private int id;
    private String phoneNumber;
    private String description;
    private List<String> photos;
    private CreateLocationDTO location;

    public UpdateCompanyDTO() {}
}
