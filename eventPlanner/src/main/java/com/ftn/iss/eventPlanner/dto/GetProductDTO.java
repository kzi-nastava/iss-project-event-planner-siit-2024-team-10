package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetProductDTO {
    private int id;
    private int categoryId;
    private boolean pending;
    private int providerID;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;

    public GetProductDTO() {}
}
