package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
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
        int totalRating = oneStarCount +
                twoStarCount * 2 +
                threeStarCount * 3 +
                fourStarCount * 4 +
                fiveStarCount * 5;

        int ratingCount = oneStarCount + twoStarCount + threeStarCount + fourStarCount + fiveStarCount;

        this.averageRating = ratingCount==0? 0 : Math.round(((double) totalRating / ratingCount) * 10) / 10.0;
    }

    public void setOneStarCount(int oneStarCount) {
        this.oneStarCount = oneStarCount;
        recalculateAverageRating();
    }

    public void setTwoStarCount(int twoStarCount) {
        this.twoStarCount = twoStarCount;
        recalculateAverageRating();
    }

    public void setThreeStarCount(int threeStarCount) {
        this.threeStarCount = threeStarCount;
        recalculateAverageRating();
    }

    public void setFourStarCount(int fourStarCount) {
        this.fourStarCount = fourStarCount;
        recalculateAverageRating();
    }

    public void setFiveStarCount(int fiveStarCount) {
        this.fiveStarCount = fiveStarCount;
        recalculateAverageRating();
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
        recalculateAverageRating();
    }

}
