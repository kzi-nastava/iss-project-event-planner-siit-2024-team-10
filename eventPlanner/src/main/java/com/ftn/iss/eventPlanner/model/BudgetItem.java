package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
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

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Offering> offerings;

    public BudgetItem() {
    }
}
