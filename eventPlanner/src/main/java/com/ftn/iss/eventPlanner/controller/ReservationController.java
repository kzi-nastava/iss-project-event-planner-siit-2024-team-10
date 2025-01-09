package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.reservation.*;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

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

    @GetMapping(value = "/{id}/service-details", produces = "application/json")
    public ResponseEntity<GetServiceDTO> getServiceDetailsByReservationId(@PathVariable("id") int id) {
        GetServiceDTO serviceDetails = reservationService.findServiceDetailsByReservationId(id);
        return new ResponseEntity<>(serviceDetails, HttpStatus.OK);
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedReservationDTO> createReservation(@RequestBody CreateReservationDTO reservation) throws Exception {
        CreatedReservationDTO createdReservation = new CreatedReservationDTO();
        // TO-DO
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedReservationDTO> updateReservation(@RequestBody UpdateReservationDTO reservation, @PathVariable("id") int id) throws Exception {
        UpdatedReservationDTO updatedReservation = new UpdatedReservationDTO();
        // TO-DO
        return new ResponseEntity<>(updatedReservation, HttpStatus.CREATED);
    }
}
