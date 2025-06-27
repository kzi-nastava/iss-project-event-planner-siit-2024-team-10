package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class BudgetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private boolean isDeleted;

    @ManyToOne
    private OfferingCategory category;
    @ManyToOne
    private Event event;

    // @OneToMany(fetch = FetchType.EAGER)
    // private Set<Offering> offerings;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<ServiceDetails> services = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<ProductDetails> products = new HashSet<>();

    public BudgetItem() {
    }
}
