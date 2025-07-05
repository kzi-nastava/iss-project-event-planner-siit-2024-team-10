package com.ftn.iss.eventPlanner.dto.offeringcategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOfferingCategoryDTO {
    private String name;
    private String description;
    private int creatorId;

    public CreateOfferingCategoryDTO(){

    }
}
