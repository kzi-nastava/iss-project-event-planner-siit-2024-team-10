package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.service.*;
import com.ftn.iss.eventPlanner.services.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetServiceDTO>> getServices(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String name
    ) {
        List<GetServiceDTO> services = serviceService.findAll(name, eventTypeId, categoryId, minPrice, maxPrice, isAvailable);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetServiceDTO>> getServices(
            Pageable pageable,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String name
    ) {
        PagedResponse<GetServiceDTO> response = serviceService.findAll(pageable, name, categoryId, eventTypeId, minPrice, maxPrice, isAvailable);
        return new ResponseEntity<PagedResponse<GetServiceDTO>>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetServiceDTO> getService(@PathVariable("id") int id, @RequestParam(required = false) LocalDateTime historyTimestamp) {
        GetServiceDTO serviceDTO = serviceService.findById(id);
        return new ResponseEntity<>(serviceDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedServiceDTO> createService(@Valid @RequestBody CreateServiceDTO service) {
        CreatedServiceDTO createdServiceDTO = serviceService.create(service);
        return new ResponseEntity<>(createdServiceDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping(value = "/{offeringId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedServiceDTO> updateService(@Valid @RequestBody UpdateServiceDTO service, @PathVariable int offeringId) {
        UpdatedServiceDTO updatedServiceDTO = serviceService.update(offeringId, service);
        return new ResponseEntity<>(updatedServiceDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @DeleteMapping("/{offeringId}")
    public ResponseEntity<?> delete(@PathVariable("offeringId") int offeringId) {
        serviceService.delete(offeringId);
        return ResponseEntity.noContent().build();
    }
}
