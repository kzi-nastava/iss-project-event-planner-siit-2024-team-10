package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccountReport {
    private int id;
    private String description;
    private LocalDateTime processingTimestamp;
    private Status status;
    private Account reporter;
    private Account reportee;

    public AccountReport() {
    }
}
