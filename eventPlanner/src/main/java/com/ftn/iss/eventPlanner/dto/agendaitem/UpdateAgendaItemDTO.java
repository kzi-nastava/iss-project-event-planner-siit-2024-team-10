package com.ftn.iss.eventPlanner.dto.agendaitem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class UpdateAgendaItemDTO {
    @NotBlank(message = "Name is required and cannot be blank")
    private String name;

    @NotBlank(message = "Description is required and cannot be blank")
    private String description;

    @NotBlank(message = "Location is required and cannot be blank")
    private String location;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;
}
