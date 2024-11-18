package com.ftn.iss.eventPlanner.model;

import java.time.LocalDateTime;

public class AccountReport {
    private String description;
    private LocalDateTime processingTimestamp;
    private Status status;
    private Account reporter;
    private Account reportee;
}
