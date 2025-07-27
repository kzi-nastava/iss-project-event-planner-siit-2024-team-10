package com.ftn.iss.eventPlanner.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CreateReservationDTO {

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Min(value = 1, message = "Event ID must be a positive number")
    private int event;

    @Min(value = 1, message = "Service ID must be a positive number")
    private int service;

    public CreateReservationDTO() {
    }
}
