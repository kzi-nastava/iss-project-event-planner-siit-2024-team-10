package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AccountReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable=false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime processingTimestamp;

    @Enumerated(EnumType.STRING)
    private Status status;

    // COMMENTED UNTIL THE NECESSARY ENTITIES ARE MADE

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "reporter_id", nullable = false)
//    private Account reporter;
//
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "reportee_id", nullable = false)
//    private Account reportee;

    public AccountReport() {
    }
}
