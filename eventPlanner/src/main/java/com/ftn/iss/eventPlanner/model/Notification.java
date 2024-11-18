package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {
    private int id;
    private boolean isRead;
    private String content;

    public Notification() {
    }
}
