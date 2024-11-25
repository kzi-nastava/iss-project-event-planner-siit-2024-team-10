package com.ftn.iss.eventPlanner.dto;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateServiceDetailsDTO {
    private int id;
    private String name;
    private String description;
    private String specification;
    private double price;
    private double discount;
    private List<String> photos;
    private int maxDuration;
    private int minDuration;
    private int cancellationPeriod;
    private int reservationPeriod;
    private boolean isVisible;
    private boolean isAvailable;
    private boolean autoConfirm;
    private LocalDateTime timestamp;
    private List<String> eventTypes;
}
