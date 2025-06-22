package com.ftn.iss.eventPlanner.dto.budgetitem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBudgetItemDTO {
    private int amount;
    private int categoryId;
    private int eventId;
}
