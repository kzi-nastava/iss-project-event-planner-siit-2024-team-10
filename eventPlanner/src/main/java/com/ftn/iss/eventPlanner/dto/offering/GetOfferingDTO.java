package com.ftn.iss.eventPlanner.dto.offering;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetOfferingDTO {
    private int id;
    private int categoryId;
    private int providerId;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    private double rating;

    public GetOfferingDTO() {
    }
}
