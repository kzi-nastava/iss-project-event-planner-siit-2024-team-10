package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    @Enumerated
    private Status status;

    @ManyToOne
    private Event event;
    @ManyToOne
    private Service service;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Reservation() {
    }
}
