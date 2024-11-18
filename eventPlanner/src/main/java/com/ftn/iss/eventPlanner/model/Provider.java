package com.ftn.iss.eventPlanner.model;

public class Provider extends User {
    private Company company;

    public Provider() {
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
