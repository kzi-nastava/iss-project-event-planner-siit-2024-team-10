package com.ftn.iss.eventPlanner.dto.reservation;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class UpdatedReservationDTO {
    private int id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Status status;
    private int eventId;
    private int serviceId;
    private LocalDateTime timestamp;

    public UpdatedReservationDTO() {
    }
}
