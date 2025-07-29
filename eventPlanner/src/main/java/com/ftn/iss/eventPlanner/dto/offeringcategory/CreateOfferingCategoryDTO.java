package com.ftn.iss.eventPlanner.dto.offeringcategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOfferingCategoryDTO {
    @NotNull(message = "Name is required")

    private String name;
    @NotNull(message = "Description is required")

    private String description;
    @Valid
    @NotNull
    private int creatorId;

    public CreateOfferingCategoryDTO(){

    }
}
