package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Organizer organizer;

    @ManyToOne
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="guests", joinColumns = @JoinColumn(name="event_id"))
    @Column(name="guest_list")
    private List<String> guestList;

    @Column(nullable = false)
    private boolean isDeleted;

    @ManyToOne
    private Location location;

    @OneToMany
    private Set<AgendaItem> agenda;

    @OneToMany
    private Set<BudgetItem> budget;

    @OneToOne
    private EventStats stats;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    public Event() {
    }

}
