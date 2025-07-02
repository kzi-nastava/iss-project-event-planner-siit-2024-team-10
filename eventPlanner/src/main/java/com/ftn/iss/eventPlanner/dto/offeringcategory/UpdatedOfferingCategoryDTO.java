package com.ftn.iss.eventPlanner.dto.offeringcategory;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedOfferingCategoryDTO {
    private int id;
    private String name;
    private String description;
    private int creatorId;
    public UpdatedOfferingCategoryDTO(){}
}
