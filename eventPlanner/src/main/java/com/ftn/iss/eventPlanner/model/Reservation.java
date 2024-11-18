package com.ftn.iss.eventPlanner.model;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private Event event;
    private Service service;

    public Reservation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
