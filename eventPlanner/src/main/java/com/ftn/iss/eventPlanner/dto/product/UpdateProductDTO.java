package com.ftn.iss.eventPlanner.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProductDTO {
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    @JsonProperty("isVisible")
    private boolean isVisible;
    @JsonProperty("isAvailable")
    private boolean isAvailable;

    public UpdateProductDTO() {}
}
