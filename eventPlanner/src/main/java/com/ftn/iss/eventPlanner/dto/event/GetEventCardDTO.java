package com.ftn.iss.eventPlanner.dto.event;

import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GetEventCardDTO {
    private int id;
    private String organizer;
    private String eventType;
    private String name;
    private LocalDate date;
    private GetLocationDTO location;
    private double averageRating;

    public GetEventCardDTO() {super();}
}
