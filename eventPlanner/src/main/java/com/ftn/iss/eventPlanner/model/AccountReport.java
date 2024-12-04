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

    @Column(nullable = false)
    @Enumerated
    private Status status;

    @ManyToOne
    private Account reporter;

    @ManyToOne
    private Account reportee;

    public AccountReport() {
    }
}
