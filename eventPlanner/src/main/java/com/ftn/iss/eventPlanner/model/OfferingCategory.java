package com.ftn.iss.eventPlanner.model;

public class OfferingCategory {
    private int id;
    private String name;
    private String description;
    private boolean isDeleted;
    private boolean pending;

    public OfferingCategory() {
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
