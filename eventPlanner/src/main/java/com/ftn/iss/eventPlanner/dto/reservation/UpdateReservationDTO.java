package com.ftn.iss.eventPlanner.dto.reservation;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateReservationDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private int eventId;
    private int serviceId;

    public UpdateReservationDTO() {
    }
}
