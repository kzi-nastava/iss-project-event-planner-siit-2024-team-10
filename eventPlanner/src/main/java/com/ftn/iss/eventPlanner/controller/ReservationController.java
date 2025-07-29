package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.reservation.*;
import com.ftn.iss.eventPlanner.services.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @GetMapping(value = "/events/{organizerId}", produces = "application/json")
    public ResponseEntity<List<GetEventDTO>> findEventsByOrganizer(@PathVariable("organizerId") int organizerId) {
        List<GetEventDTO> events = reservationService.findEventsByOrganizer(organizerId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReservationDTO> createReservation(@Valid @RequestBody CreateReservationDTO reservation) {
        CreatedReservationDTO createdReservation = reservationService.create(reservation);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") int id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @GetMapping(value = "/{providerId}/pending", produces = "application/json")
    public ResponseEntity<Collection<GetReservationDTO>> getPendingReservationsByProvider(@PathVariable("providerId") int providerId) {
        List<GetReservationDTO> reservations = reservationService.findPendingReservations(providerId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping(value="/{reservationId}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> acceptReservation(@PathVariable("reservationId") int reservationId) {
        reservationService.acceptReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping(value="/{reservationId}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> rejectReservation(@PathVariable("reservationId") int reservationId) {
        reservationService.rejectReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
