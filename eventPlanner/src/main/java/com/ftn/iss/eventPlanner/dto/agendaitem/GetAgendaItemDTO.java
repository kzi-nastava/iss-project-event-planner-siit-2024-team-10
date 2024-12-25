package com.ftn.iss.eventPlanner.dto.agendaitem;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class GetAgendaItemDTO {
    private int id;
    private String name;
    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
}
