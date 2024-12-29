package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)

    private int score;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account rater;

    public Rating() {
    }
}
