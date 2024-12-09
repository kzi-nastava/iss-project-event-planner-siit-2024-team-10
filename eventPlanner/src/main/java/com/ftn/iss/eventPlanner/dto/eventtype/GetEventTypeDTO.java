package com.ftn.iss.eventPlanner.dto.eventtype;

import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetEventTypeDTO {
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private List<GetOfferingCategoryDTO> recommendedCategories;
}
