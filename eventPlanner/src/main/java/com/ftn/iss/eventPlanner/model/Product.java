package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
@Getter
@Setter
@Entity
public class Product extends Offering {
    @OneToOne(cascade = CascadeType.ALL)
    private OfferingDetails currentDetails;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<OfferingDetails> detailsHistory = new HashSet<>();

    public Product() {
    }
}
