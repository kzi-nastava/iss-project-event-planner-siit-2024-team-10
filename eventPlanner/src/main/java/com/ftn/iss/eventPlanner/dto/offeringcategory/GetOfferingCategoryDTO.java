package com.ftn.iss.eventPlanner.dto.offeringcategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOfferingCategoryDTO {
    private int id;
    private String name;
    private String description;
    private boolean isDeleted;
    private boolean pending;
}
