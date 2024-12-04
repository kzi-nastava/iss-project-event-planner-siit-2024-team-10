package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable=false)
    private boolean isRead;

    @Column(nullable=false)
    private String content;

    public Notification() {
    }
}
