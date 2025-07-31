package com.ftn.iss.eventPlanner.dto.budgetitem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBudgetItemDTO {
    @NotNull(message = "Amount is required and cannot be blank")

    private int amount;
    @NotNull

    private int categoryId;
}
