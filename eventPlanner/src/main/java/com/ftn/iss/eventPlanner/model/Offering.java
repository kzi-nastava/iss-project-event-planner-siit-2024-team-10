package com.ftn.iss.eventPlanner.model;

import java.util.List;

public abstract class Offering {
    private int id;
    private boolean pending;
    private boolean isDeleted;
    private Provider provider;
    private List<Comment> comments;
    private List<Rating> ratings;
}
