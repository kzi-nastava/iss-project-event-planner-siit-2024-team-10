package com.ftn.iss.eventPlanner.dto.offeringcategory;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeOfferingCategoryDTO {
    @NotNull
    int categoryId;
}
