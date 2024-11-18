package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private int id;
    private String content;
    private Status status;
    private Account commenter;

    public Comment() {
    }
}
