package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Message {
    private int id;
    private String content;
    private LocalDateTime timestamp;
    private Account sender;
    private Account receiver;
    private boolean isRead;

    public Message() {
    }
}
