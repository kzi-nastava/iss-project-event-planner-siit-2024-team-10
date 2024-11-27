package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.CreateReservationDTO;
import com.ftn.iss.eventPlanner.dto.CreatedReservationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReservationDTO> createProduct(@RequestBody CreateReservationDTO reservation) throws Exception {
        CreatedReservationDTO createdReservation = new CreatedReservationDTO();
        createdReservation.setId(1);
        createdReservation.setStatus(reservation.getStatus());
        createdReservation.setStartTime(reservation.getStartTime());
        createdReservation.setEndTime(reservation.getEndTime());
        createdReservation.setEventId(reservation.getEventId());
        createdReservation.setServiceId(reservation.getServiceId());

        return new ResponseEntity<CreatedReservationDTO>(createdReservation, HttpStatus.CREATED);
    }
}
