package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EventStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private int oneStarCount;
    @Column
    private int twoStarCount;
    @Column
    private int threeStarCount;
    @Column
    private int fourStarCount;
    @Column
    private int fiveStarCount;
    @Column
    private int participantsCount;
}
