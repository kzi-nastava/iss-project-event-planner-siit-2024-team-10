package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
@Getter
@Setter
@AllArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    @Enumerated
    private Status status;
    @ManyToOne
    private Account commenter;
    @Column(nullable = false)
    private int rating;

    public Comment() {
    }
}
