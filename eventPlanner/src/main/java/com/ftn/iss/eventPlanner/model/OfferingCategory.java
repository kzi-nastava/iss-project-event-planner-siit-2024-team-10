package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferingCategory {
    private int id;
    private String name;
    private String description;
    private boolean isDeleted;
    private boolean pending;

    public OfferingCategory() {
    }
}
