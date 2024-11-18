package com.ftn.iss.eventPlanner.model;

import java.time.LocalDate;
import java.util.List;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
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

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<String> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<String> guestList) {
        this.guestList = guestList;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<AgendaItem> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<AgendaItem> agenda) {
        this.agenda = agenda;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<BudgetItem> getBudget() {
        return budget;
    }

    public void setBudget(List<BudgetItem> budget) {
        this.budget = budget;
    }
}
