package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Comment;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.model.Provider;
import com.ftn.iss.eventPlanner.model.Rating;
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
