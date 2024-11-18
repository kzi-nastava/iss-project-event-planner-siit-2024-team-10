package com.ftn.iss.eventPlanner.model;

import java.util.List;

public class Product extends Offering {
    private ProductDetails currentDetails;
    private List<ProductDetails> detailsHistory;

    public Product() {
    }

    public ProductDetails getCurrentDetails() {
        return currentDetails;
    }

    public void setCurrentDetails(ProductDetails currentDetails) {
        this.currentDetails = currentDetails;
    }

    public List<ProductDetails> getDetailsHistory() {
        return detailsHistory;
    }

    public void setDetailsHistory(List<ProductDetails> detailsHistory) {
        this.detailsHistory = detailsHistory;
    }
}
