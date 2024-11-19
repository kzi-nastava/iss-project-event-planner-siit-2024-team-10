package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class AgendaItem {
    private int id;
    private String name;
    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isDeleted;

    public AgendaItem() {
    }
}
