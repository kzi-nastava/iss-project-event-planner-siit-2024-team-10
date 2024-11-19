package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating {
    private int id;
    private int score;
    private Account rater;

    public Rating() {
    }
}
