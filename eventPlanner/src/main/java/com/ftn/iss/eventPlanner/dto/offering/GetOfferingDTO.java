package com.ftn.iss.eventPlanner.dto.offering;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetOfferingDTO {
    private int id;
    private GetOfferingCategoryDTO category;
    private GetProviderDTO provider;
    private String name;
    private String description;
    private String specification;
    private GetLocationDTO location;
    private double price;
    private double discount;
    private double averageRating;
    @JsonProperty("isProduct")
    private boolean isProduct;
    private List<String> photos;

    public GetOfferingDTO() {
    }
}
