package com.ftn.iss.eventPlanner.dto.agendaitem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CreateAgendaItemDTO {
    private String name;
    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
}
