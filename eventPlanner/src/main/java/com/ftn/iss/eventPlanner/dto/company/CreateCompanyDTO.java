package com.ftn.iss.eventPlanner.dto.company;

import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateCompanyDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Description is required")
    private String description;

    private List<String> photos;

    @Valid
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
