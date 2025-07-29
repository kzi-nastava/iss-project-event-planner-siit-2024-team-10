package com.ftn.iss.eventPlanner.dto.offeringcategory;

import com.ftn.iss.eventPlanner.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOfferingCategoryDTO {
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private int creatorId;
    public UpdateOfferingCategoryDTO(){}
}
