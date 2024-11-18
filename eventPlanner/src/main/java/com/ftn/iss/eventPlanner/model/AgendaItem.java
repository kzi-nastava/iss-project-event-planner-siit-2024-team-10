package com.ftn.iss.eventPlanner.model;

import java.time.LocalTime;

public class AgendaItem {
    private int id;
    private String name;
    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isDeleted;
}
