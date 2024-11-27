package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.CreateReservationDTO;
import com.ftn.iss.eventPlanner.dto.CreatedReservationDTO;
import com.ftn.iss.eventPlanner.dto.GetReservationDTO;
import com.ftn.iss.eventPlanner.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @GetMapping(produces = "application/json")
    public ResponseEntity<Collection<GetReservationDTO>> getReservations() {
        Collection<GetReservationDTO> reservations = new ArrayList<>();

        GetReservationDTO reservation1 = new GetReservationDTO();
        reservation1.setId(1);
        reservation1.setStartTime(LocalDateTime.of(2024, 11, 27, 10, 0));
        reservation1.setEndTime(LocalDateTime.of(2024, 11, 27, 12, 0));
        reservation1.setStatus(Status.ACCEPTED);
        reservation1.setEventId(101);
        reservation1.setServiceId(201);
        reservations.add(reservation1);

        GetReservationDTO reservation2 = new GetReservationDTO();
        reservation2.setId(2);
        reservation2.setStartTime(LocalDateTime.of(2024, 11, 28, 14, 0));
        reservation2.setEndTime(LocalDateTime.of(2024, 11, 28, 16, 0));
        reservation2.setStatus(Status.PENDING);
        reservation2.setEventId(102);
        reservation2.setServiceId(202);
        reservations.add(reservation2);

        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<GetReservationDTO> getReservation(@PathVariable("id") int id) {
        GetReservationDTO reservation = new GetReservationDTO();
        reservation.setId(id);
        reservation.setStartTime(LocalDateTime.of(2024, 11, 27, 10, 0));
        reservation.setEndTime(LocalDateTime.of(2024, 11, 27, 12, 0));
        reservation.setStatus(Status.PENDING);
        reservation.setEventId(101);
        reservation.setServiceId(201);

        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

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