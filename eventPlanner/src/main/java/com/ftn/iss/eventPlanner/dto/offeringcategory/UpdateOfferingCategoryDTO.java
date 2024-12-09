package com.ftn.iss.eventPlanner.dto.offeringcategory;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOfferingCategoryDTO {
    private String name;
    private String description;
    public UpdateOfferingCategoryDTO(){}
}
