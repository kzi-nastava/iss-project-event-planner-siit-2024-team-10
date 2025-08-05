package com.ftn.iss.eventPlanner.dto.budgetitem;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBudgetItemDTO {
    private int amount;
    public UpdateBudgetItemDTO() {}

    public UpdateBudgetItemDTO(int amount){
        this.amount=amount;
    }
}
