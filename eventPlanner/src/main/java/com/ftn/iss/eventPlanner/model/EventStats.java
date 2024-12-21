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
    @Column(nullable = false)
    private double averageRating;

    public void recalculateAverageRating() {
        if (participantsCount == 0) {
            this.averageRating = 0.0;
        } else {
            int totalRating = oneStarCount +
                    twoStarCount * 2 +
                    threeStarCount * 3 +
                    fourStarCount * 4 +
                    fiveStarCount * 5;

            this.averageRating = Math.round(((double) totalRating / participantsCount) * 10) / 10.0;
        }
    }


}
