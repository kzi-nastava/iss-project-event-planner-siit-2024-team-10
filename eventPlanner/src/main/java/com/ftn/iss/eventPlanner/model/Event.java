package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //private Organizer organizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_type_id")
    private EventType eventType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false)
    private boolean isOpen;

    @Column(nullable = false)
    private LocalDate date;

    @ElementCollection
    @CollectionTable(name="guests", joinColumns = @JoinColumn(name="event_id"))
    @Column(name="guest_list")
    private List<String> guestList;

    @Column(nullable = false)
    private boolean isDeleted;

    //private Location location;

    @OneToMany(fetch = FetchType.LAZY)
    private List<AgendaItem> agenda;
    //private List<Comment> comments;
    //private List<Rating> ratings;

    @OneToMany(fetch = FetchType.LAZY)
    private List<BudgetItem> budget;

    public Event() {
    }

}
