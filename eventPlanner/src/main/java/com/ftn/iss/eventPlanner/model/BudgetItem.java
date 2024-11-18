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

    public BudgetItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public OfferingCategory getCategory() {
        return category;
    }

    public void setCategory(OfferingCategory category) {
        this.category = category;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }
}
