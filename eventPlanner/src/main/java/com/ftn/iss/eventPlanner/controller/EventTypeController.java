package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.eventtype.CreateEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.CreatedEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.UpdateEventTypeDTO;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.services.EventTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-types")
public class EventTypeController {
    @Autowired
    private EventTypeService eventTypeService;

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
}
