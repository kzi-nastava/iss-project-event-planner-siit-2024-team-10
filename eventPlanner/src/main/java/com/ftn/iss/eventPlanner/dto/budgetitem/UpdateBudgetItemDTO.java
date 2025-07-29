package com.ftn.iss.eventPlanner.dto.budgetitem;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBudgetItemDTO {
    @NotNull
    private int amount;

}
