package com.ftn.iss.eventPlanner.model;

import java.util.List;

public class EventType {
    private int id;
    private String name;
    private String description;
    private boolean isActive;
    private List<OfferingCategory> recommendedCategories;

    public EventType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<OfferingCategory> getRecommendedCategories() {
        return recommendedCategories;
    }

    public void setRecommendedCategories(List<OfferingCategory> recommendedCategories) {
        this.recommendedCategories = recommendedCategories;
    }
}
