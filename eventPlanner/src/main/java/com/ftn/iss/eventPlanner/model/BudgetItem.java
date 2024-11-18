package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
public class BudgetItem {
    private int id;
    private double amount;
    private LocalDateTime purchaseDate;
    private boolean isDeleted;
    private OfferingCategory category;
    private Offering offering;

    public BudgetItem() {
    }
}
