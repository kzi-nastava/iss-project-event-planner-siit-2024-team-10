package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetEventDTO;
import com.ftn.iss.eventPlanner.model.Event;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getTopEvents() {
        Collection<GetEventDTO> events = new ArrayList<>();

        GetEventDTO event1 = new GetEventDTO();
        event1.setId(1);
        event1.setOrganizerId(101);
        event1.setEventTypeId(201);
        event1.setName("Top Event 1");
        event1.setDescription("This is the top event 1");
        event1.setMaxParticipants(100);
        event1.setOpen(true);
        event1.setDate(LocalDate.now().plusDays(1));
        event1.setLocationId(301);

        GetEventDTO event2 = new GetEventDTO();
        event2.setId(2);
        event2.setOrganizerId(102);
        event2.setEventTypeId(202);
        event2.setName("Top Event 2");
        event2.setDescription("This is the top event 2");
        event2.setMaxParticipants(150);
        event2.setOpen(true);
        event2.setDate(LocalDate.now().plusDays(2));
        event2.setLocationId(302);

        events.add(event1);
        events.add(event2);

        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
