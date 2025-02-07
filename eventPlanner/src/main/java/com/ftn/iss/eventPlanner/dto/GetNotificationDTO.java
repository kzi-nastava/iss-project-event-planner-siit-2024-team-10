package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetNotificationDTO {
    private int id;
    private String title;
    private String description;
    private LocalDateTime date;
    private boolean read;
}
