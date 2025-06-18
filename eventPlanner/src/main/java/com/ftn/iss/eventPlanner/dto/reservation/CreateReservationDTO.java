package com.ftn.iss.eventPlanner.dto.reservation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CreateReservationDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private int event;
    private int service;

    public CreateReservationDTO() {
    }
}
