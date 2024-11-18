package com.ftn.iss.eventPlanner.model;

import java.time.LocalDateTime;
import java.util.List;

public class ProductDetails {
    private int id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;
    private LocalDateTime timestamp;
    private List<EventType> eventTypes;
}
