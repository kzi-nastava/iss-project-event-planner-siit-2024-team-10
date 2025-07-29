package com.ftn.iss.eventPlanner.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLocationDTO {
    @NotNull(message = "City cannot be null")
    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotNull(message = "Country cannot be null")
    @NotBlank(message = "Country cannot be blank")
    private String country;

    @NotNull(message = "Street cannot be null")
    @NotBlank(message = "Street cannot be blank")
    private String street;

    @NotNull(message = "House number cannot be null")
    @NotBlank(message = "House number cannot be blank")
    private String houseNumber;

    public CreateLocationDTO() {}

    public CreateLocationDTO(String city, String country, String street, String houseNumber) {
        this.city = city;
        this.country = country;
        this.street = street;
        this.houseNumber = houseNumber;
    }
}
