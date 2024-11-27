package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedPricelistItemDTO {
    private int id;
    private String name;
    private double price;
    private double discount;

    public CreatedPricelistItemDTO() {
    }
}
