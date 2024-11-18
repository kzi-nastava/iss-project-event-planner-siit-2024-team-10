package com.ftn.iss.eventPlanner.model;

import java.util.List;

public class Service extends Offering{
    private ServiceDetails currentDetails;
    private List<ServiceDetails> detailsHistory;

    public Service() {
    }

    public ServiceDetails getCurrentDetails() {
        return currentDetails;
    }

    public void setCurrentDetails(ServiceDetails currentDetails) {
        this.currentDetails = currentDetails;
    }

    public List<ServiceDetails> getDetailsHistory() {
        return detailsHistory;
    }

    public void setDetailsHistory(List<ServiceDetails> detailsHistory) {
        this.detailsHistory = detailsHistory;
    }
}
