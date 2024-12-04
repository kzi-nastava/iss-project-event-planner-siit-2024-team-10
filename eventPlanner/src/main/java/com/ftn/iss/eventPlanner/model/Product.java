package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Product extends Offering {
    @OneToOne
    private ProductDetails currentDetails;
    @OneToMany
    private Set<ProductDetails> detailsHistory = new HashSet<>();

    public Product() {
    }
}
