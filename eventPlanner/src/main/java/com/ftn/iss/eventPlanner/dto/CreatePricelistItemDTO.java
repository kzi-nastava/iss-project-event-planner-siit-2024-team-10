package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePricelistItemDTO {
    private String name;
    private double price;
    private double discount;

    public CreatePricelistItemDTO() {
    }
}
