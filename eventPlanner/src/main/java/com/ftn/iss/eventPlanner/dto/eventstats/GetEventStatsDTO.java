package com.ftn.iss.eventPlanner.dto.eventstats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEventStatsDTO {
    private int id;
    private int oneStarCount;
    private int twoStarCount;
    private int threeStarCount;
    private int fourStarCount;
    private int fiveStarCount;
    private int participantsCount;
    private double averageRating;
    private String eventName;
}
