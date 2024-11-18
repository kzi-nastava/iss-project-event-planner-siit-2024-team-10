package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Event {
    private int id;
    private Organizer organizer;
    private EventType eventType;
    private String name;
    private String description;
    private int maxParticipants;
    private boolean isOpen;
    private LocalDate date;
    private List<String> guestList;
    private boolean isDeleted;
    private Location location;
    private List<AgendaItem> agenda;
    private List<Comment> comments;
    private List<Rating> ratings;
    private List<BudgetItem> budget;

    public Event() {
    }

}
