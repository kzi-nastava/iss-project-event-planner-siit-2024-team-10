package com.ftn.iss.eventPlanner.dto.event;

import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.user.GetOrganizerDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class GetEventDTO {
    private int id;
    private String name;
    private GetOrganizerDTO organizer;
    private GetEventTypeDTO eventType;
    private String description;
    private int maxParticipants;
    private boolean isOpen;
    private LocalDateTime date;
    private GetLocationDTO location;
    private double averageRating;

    public GetEventDTO() {super();}
}
