package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.eventtype.*;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.services.EventTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/event-types")
public class EventTypeController {
    @Autowired
    private EventTypeService eventTypeService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedEventTypeDTO> createEventType(@Valid @RequestBody CreateEventTypeDTO createEventTypeDTO) {
        CreatedEventTypeDTO createdEventType = eventTypeService.create(createEventTypeDTO);
        return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);}

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetEventTypeDTO>> getAllEventTypes() {
        List<GetEventTypeDTO> eventTypes = eventTypeService.findAll();
        return new ResponseEntity<>(eventTypes, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetEventTypeDTO> getEventTypeById(@PathVariable int id) {
        GetEventTypeDTO eventType = eventTypeService.findById(id);
        return new ResponseEntity<>(eventType, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedEventTypeDTO> updateEventType(@PathVariable int id, @Valid @RequestBody UpdateEventTypeDTO updateEventTypeDTO) {
        UpdatedEventTypeDTO updatedEventType = eventTypeService.update(id, updateEventTypeDTO);
        return new ResponseEntity<>(updatedEventType, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable int id) {
        eventTypeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/{id}/activate")
    public ResponseEntity<Void> activateEventType(@PathVariable int id) {
        eventTypeService.activate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
