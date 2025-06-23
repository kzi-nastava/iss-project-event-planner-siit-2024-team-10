package com.ftn.iss.eventPlanner.dto.budgetitem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedBudgetItemDTO {
    int id;
    private int amount;
    private int offeringId;
}
