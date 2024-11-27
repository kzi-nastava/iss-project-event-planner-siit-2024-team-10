package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedPricelistItemDTO {
    private int id;
    private String name;
    private double price;
    private double discount;

    public UpdatedPricelistItemDTO() {
    }
}
