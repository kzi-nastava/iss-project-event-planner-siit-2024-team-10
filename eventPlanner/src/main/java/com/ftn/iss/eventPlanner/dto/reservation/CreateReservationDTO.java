package com.ftn.iss.eventPlanner.dto.reservation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class CreateReservationDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private int eventId;
    private int serviceId;
    private LocalDateTime timestamp;

    public CreateReservationDTO() {
    }
}
