package com.ftn.iss.eventPlanner.model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private Event event;
    private Service service;
}
