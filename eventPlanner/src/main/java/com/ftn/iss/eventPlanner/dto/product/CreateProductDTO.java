package com.ftn.iss.eventPlanner.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateProductDTO {
    private int categoryId;
    private String categoryProposalName;
    private String categoryProposalDescription;
    private int providerID;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;

    public CreateProductDTO() {}
}
