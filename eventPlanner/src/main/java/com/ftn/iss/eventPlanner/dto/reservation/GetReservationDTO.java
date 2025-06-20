package com.ftn.iss.eventPlanner.dto.reservation;

import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class GetReservationDTO {
    private int id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Status status;
    private GetEventDTO event;
    private GetServiceDTO service;

    public GetReservationDTO() {
    }
}
