package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRatingDTO {
    private int id;
    private int score;
    private int accountId;
    public GetRatingDTO(){

    }
}
