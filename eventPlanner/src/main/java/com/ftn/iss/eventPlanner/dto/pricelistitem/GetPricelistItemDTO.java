package com.ftn.iss.eventPlanner.dto.pricelistitem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPricelistItemDTO {
    private int id;
    private int offeringId;
    private String name;
    private double price;
    private double discount;

    public GetPricelistItemDTO() {
    }
}
