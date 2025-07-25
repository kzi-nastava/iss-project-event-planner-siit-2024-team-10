package com.ftn.iss.eventPlanner.dto.company;

import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompanyDTO {
    @NotBlank(message = "Phone number must not be blank")
    private String phoneNumber;

    @NotBlank(message = "Phone number must not be blank")
    private String description;

    @Valid
    @NotNull(message = "Location must not be null")
    private CreateLocationDTO location;

    public UpdateCompanyDTO() {}
}
