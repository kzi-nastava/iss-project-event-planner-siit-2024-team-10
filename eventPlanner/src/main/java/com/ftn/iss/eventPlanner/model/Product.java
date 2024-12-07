package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class Product extends Offering {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_product_details_id")
    private ProductDetails currentDetails;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ProductDetails> detailsHistory = new HashSet<>();

    public Product() {
    }
}