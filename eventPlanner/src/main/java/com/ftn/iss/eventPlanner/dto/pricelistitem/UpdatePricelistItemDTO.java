package com.ftn.iss.eventPlanner.dto.pricelistitem;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePricelistItemDTO {
    @NotNull

    private double price;
    @NotNull
    private double discount;

    public UpdatePricelistItemDTO() {
    }
}
