package com.ftn.iss.eventPlanner.dto.budgetitem;

import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetBudgetItemDTO {
    private int id;
    private int amount;
    private List<GetOfferingDTO> offerings;
    private GetOfferingCategoryDTO Category;
    private boolean isDeleted;
}
