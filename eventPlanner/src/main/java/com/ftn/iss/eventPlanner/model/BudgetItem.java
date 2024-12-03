package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

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
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private boolean isDeleted;

    @ManyToOne
    private OfferingCategory category;

    @ManyToOne
    private Offering offering;

    public BudgetItem() {
    }
}
