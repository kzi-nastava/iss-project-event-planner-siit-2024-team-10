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
        try{
            CreatedEventTypeDTO createdEventType = eventTypeService.create(createEventTypeDTO);
            return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetEventTypeDTO>> getAllEventTypes() {
        List<GetEventTypeDTO> eventTypes = eventTypeService.findAll();
        return new ResponseEntity<>(eventTypes, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetEventTypeDTO> getEventTypeById(@PathVariable int id) {
        try {
            GetEventTypeDTO eventType = eventTypeService.findById(id);
            return new ResponseEntity<>(eventType, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedEventTypeDTO> updateEventType(@PathVariable int id, @Valid @RequestBody UpdateEventTypeDTO updateEventTypeDTO) {
        try {
            UpdatedEventTypeDTO updatedEventType = eventTypeService.update(id, updateEventTypeDTO);
            return new ResponseEntity<>(updatedEventType, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable int id) {
        try {
            eventTypeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/{id}/activate")
    public ResponseEntity<Void> activateEventType(@PathVariable int id) {
        try {
            eventTypeService.activate(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
