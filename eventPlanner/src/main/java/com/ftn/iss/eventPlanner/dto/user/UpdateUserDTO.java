package com.ftn.iss.eventPlanner.dto.user;

import com.ftn.iss.eventPlanner.dto.company.UpdateCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @NotBlank(message = "Phone number must not be blank")
    private String phoneNumber;

    @NotNull(message = "Location must not be null")
    @Valid
    private CreateLocationDTO location;

    public UpdateUserDTO() {}
}
