package com.ftn.iss.eventPlanner.dto.event;

import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatedEventDTO {
    int id;
    private int eventTypeId;
    private int organizerId;
    private String name;
    private String description;
    private int maxParticipants;
    private boolean isOpen;
    private LocalDate date;
    private boolean isDeleted;
    private CreatedLocationDTO location;
}
