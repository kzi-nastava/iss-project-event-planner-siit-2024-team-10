package com.ftn.iss.eventPlanner.dto.budgetitem;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyRequestDTO {
    @NotNull
    private boolean pending;
    public BuyRequestDTO(boolean reservationBased) {
        this.pending = reservationBased;
    }
}
