package com.ftn.iss.eventPlanner.dto.offeringcategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedOfferingCategoryDTO {
    private int id;
    private String name;
    private String description;
    private int creatorId;

    public CreatedOfferingCategoryDTO(){

    }
}
