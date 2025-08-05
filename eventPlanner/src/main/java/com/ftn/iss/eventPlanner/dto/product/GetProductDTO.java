package com.ftn.iss.eventPlanner.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetProductDTO {
    private int id;
    private GetOfferingCategoryDTO category;
    private boolean pending;
    private GetProviderDTO provider;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    @JsonProperty("visible")
    private boolean isVisible;
    @JsonProperty("available")
    private boolean isAvailable;
    private boolean deleted;

    public GetProductDTO() {}
}
