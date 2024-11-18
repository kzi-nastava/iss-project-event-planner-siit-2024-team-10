package com.ftn.iss.eventPlanner.model;

import java.util.List;

public class EventType {
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private List<OfferingCategory> recommendedCategories;
}
