package com.ftn.iss.eventPlanner.model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String content;
    private LocalDateTime timestamp;
    private Account sender;
    private Account receiver;
}
