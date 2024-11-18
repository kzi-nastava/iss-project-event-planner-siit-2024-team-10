package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Product extends Offering {
    private ProductDetails currentDetails;
    private List<ProductDetails> detailsHistory;

    public Product() {
    }
}
