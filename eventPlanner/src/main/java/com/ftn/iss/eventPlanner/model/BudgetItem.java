package com.ftn.iss.eventPlanner.model;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class BudgetItem {
    private int id;
    private double amount;
    private LocalDateTime purchaseDate;
    private boolean isDeleted;
    private OfferingCategory category;
    private Offering offering;
}
