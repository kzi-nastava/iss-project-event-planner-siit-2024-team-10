package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.CreatedEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.reservation.*;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<Collection<GetReservationDTO>> getReservations() {
        List<GetReservationDTO> reservations = reservationService.findAll();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<GetReservationDTO> getReservation(@PathVariable("id") int id) {
        GetReservationDTO reservation = reservationService.findById(id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @GetMapping(value = "/{serviceId}", produces = "application/json")
    public ResponseEntity<Collection<GetReservationDTO>> getReservationsByService(@PathVariable("serviceId") int serviceId) {
        List<GetReservationDTO> reservations = reservationService.findByServiceId(serviceId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }


    @GetMapping(value = "/{organizerId}", produces = "application/json")
    public ResponseEntity<Collection<GetReservationDTO>> getReservationsByOrganizer(@PathVariable("organizerId") int organizerId) {
        List<GetReservationDTO> reservations = reservationService.findByOrganizerId(organizerId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping(value = "/{providerId}", produces = "application/json")
    public ResponseEntity<Collection<GetReservationDTO>> getReservationsByProvider(@PathVariable("providerId") int providerId) {
        List<GetReservationDTO> reservations = reservationService.findByProviderId(providerId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @GetMapping(value = "/events/{organizerId}", produces = "application/json")
    public ResponseEntity<List<GetEventDTO>> findEventsByOrganizer(@PathVariable("organizerId") int organizerId) {
        try{
            List<GetEventDTO> events = reservationService.findEventsByOrganizer(organizerId);
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReservationDTO> createReservation(@RequestBody CreateReservationDTO reservation) throws Exception {
        CreatedReservationDTO createdReservation = reservationService.create(reservation);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") int id) throws Exception {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
