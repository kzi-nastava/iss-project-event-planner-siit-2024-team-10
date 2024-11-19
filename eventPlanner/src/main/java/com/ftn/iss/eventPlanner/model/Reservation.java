package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Reservation {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private Event event;
    private Service service;

    public Reservation() {
    }
}
