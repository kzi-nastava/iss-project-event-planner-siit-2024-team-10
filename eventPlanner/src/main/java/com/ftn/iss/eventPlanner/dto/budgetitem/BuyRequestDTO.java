package com.ftn.iss.eventPlanner.dto.budgetitem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyRequestDTO {
    private boolean pending;
    public BuyRequestDTO(boolean reservationBased) {
        this.pending = reservationBased;
    }
}
