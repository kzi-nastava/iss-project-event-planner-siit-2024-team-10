package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventType {
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private List<OfferingCategory> recommendedCategories;

    public EventType() {
    }
}
