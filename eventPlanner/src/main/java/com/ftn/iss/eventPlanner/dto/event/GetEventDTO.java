package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class GetEventDTO {
    private int id;
    private int organizerId;
    private int eventTypeId;
    private String name;
    private String description;
    private int maxParticipants;
    private boolean isOpen;
    private LocalDate date;
    private GetLocationDTO location;

    public GetEventDTO() {super();}
}
