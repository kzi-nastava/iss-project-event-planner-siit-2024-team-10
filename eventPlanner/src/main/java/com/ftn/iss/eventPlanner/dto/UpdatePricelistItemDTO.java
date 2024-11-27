package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePricelistItemDTO {
    private double price;
    private double discount;

    public UpdatePricelistItemDTO() {
    }
}
