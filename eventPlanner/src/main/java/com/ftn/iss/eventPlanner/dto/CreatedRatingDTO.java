package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedRatingDTO {
    private int id;
    private int score;
    private int accountId;
    public CreatedRatingDTO(){

    }

}
