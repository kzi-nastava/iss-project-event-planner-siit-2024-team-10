package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.LocationDTO;
import com.ftn.iss.eventPlanner.dto.PagedResponse;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.RequestContextFilter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getTopEvents() {
        Collection<GetEventDTO> events = new ArrayList<>();

        GetEventDTO event1 = new GetEventDTO();
        event1.setId(1);
        event1.setOrganizerId(1001);
        event1.setEventTypeId(2001);
        event1.setName("Concert Night");
        event1.setDescription("An unforgettable concert experience with top artists.");
        event1.setMaxParticipants(500);
        event1.setOpen(true);
        event1.setDate(LocalDate.now().plusDays(7));
        event1.setLocation(new LocationDTO("Belgrade","Serbia","Street","2"));

        GetEventDTO event2 = new GetEventDTO();
        event2.setId(2);
        event2.setOrganizerId(1002);
        event2.setEventTypeId(2002);
        event2.setName("Art Exhibition");
        event2.setDescription("Explore stunning artworks by renowned artists.");
        event2.setMaxParticipants(300);
        event2.setOpen(true);
        event2.setDate(LocalDate.now().plusDays(15));
        event2.setLocation(new LocationDTO("Belgrade","Serbia","Street","3"));

        GetEventDTO event3 = new GetEventDTO();
        event3.setId(3);
        event3.setOrganizerId(1003);
        event3.setEventTypeId(2002);
        event3.setName("Tech Conference");
        event3.setDescription("Discover the latest trends in technology.");
        event3.setMaxParticipants(1000);
        event3.setOpen(false);
        event3.setDate(LocalDate.now().plusDays(30));
        event3.setLocation(new LocationDTO("Novi Sad","Serbia","Street","2"));

        GetEventDTO event4 = new GetEventDTO();
        event4.setId(4);
        event4.setOrganizerId(1004);
        event4.setEventTypeId(2004);
        event4.setName("Charity Gala");
        event4.setDescription("A glamorous evening to support a good cause.");
        event4.setMaxParticipants(200);
        event4.setOpen(true);
        event4.setDate(LocalDate.now().plusDays(10));
        event4.setLocation(new LocationDTO("Novi Sad","Serbia","Street","4"));

        GetEventDTO event5 = new GetEventDTO();
        event5.setId(5);
        event5.setOrganizerId(1005);
        event5.setEventTypeId(2005);
        event5.setName("Marathon");
        event5.setDescription("Join the annual marathon and test your endurance.");
        event5.setMaxParticipants(2000);
        event5.setOpen(true);
        event5.setDate(LocalDate.now().plusDays(3));
        event5.setLocation(new LocationDTO("Arilje","Serbia","Street","10"));

        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping(value="/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getEvents(
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String name
    ) {
        Collection<GetEventDTO> events = new ArrayList<>();

        GetEventDTO event1 = new GetEventDTO();
        event1.setId(1);
        event1.setOrganizerId(1001);
        event1.setEventTypeId(2001);
        event1.setName("Concert Night");
        event1.setDescription("An unforgettable concert experience with top artists.");
        event1.setMaxParticipants(500);
        event1.setOpen(true);
        event1.setDate(LocalDate.now().plusDays(7));
        event1.setLocation(new LocationDTO("Belgrade","Serbia","Street","2"));

        GetEventDTO event2 = new GetEventDTO();
        event2.setId(2);
        event2.setOrganizerId(1002);
        event2.setEventTypeId(2002);
        event2.setName("Art Exhibition");
        event2.setDescription("Explore stunning artworks by renowned artists.");
        event2.setMaxParticipants(300);
        event2.setOpen(true);
        event2.setDate(LocalDate.now().plusDays(15));
        event2.setLocation(new LocationDTO("Belgrade","Serbia","Street","3"));

        GetEventDTO event3 = new GetEventDTO();
        event3.setId(3);
        event3.setOrganizerId(1003);
        event3.setEventTypeId(2002);
        event3.setName("Tech Conference");
        event3.setDescription("Discover the latest trends in technology.");
        event3.setMaxParticipants(1000);
        event3.setOpen(false);
        event3.setDate(LocalDate.now().plusDays(30));
        event3.setLocation(new LocationDTO("Novi Sad","Serbia","Street","2"));

        events.add(event1);
        events.add(event2);
        events.add(event3);

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetEventDTO>> getEvents(
            SpringDataWebProperties.Pageable page,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String name
    ) {
        Collection<GetEventDTO> events = new ArrayList<>();

        GetEventDTO event1 = new GetEventDTO();
        event1.setId(1);
        event1.setOrganizerId(1001);
        event1.setEventTypeId(2001);
        event1.setName("Concert Night");
        event1.setDescription("An unforgettable concert experience with top artists.");
        event1.setMaxParticipants(500);
        event1.setOpen(true);
        event1.setDate(LocalDate.now().plusDays(7));
        event1.setLocation(new LocationDTO("Belgrade","Serbia","Street","2"));

        GetEventDTO event2 = new GetEventDTO();
        event2.setId(2);
        event2.setOrganizerId(1002);
        event2.setEventTypeId(2002);
        event2.setName("Art Exhibition");
        event2.setDescription("Explore stunning artworks by renowned artists.");
        event2.setMaxParticipants(300);
        event2.setOpen(true);
        event2.setDate(LocalDate.now().plusDays(15));
        event2.setLocation(new LocationDTO("Belgrade","Serbia","Street","3"));

        GetEventDTO event3 = new GetEventDTO();
        event3.setId(3);
        event3.setOrganizerId(1003);
        event3.setEventTypeId(2002);
        event3.setName("Tech Conference");
        event3.setDescription("Discover the latest trends in technology.");
        event3.setMaxParticipants(1000);
        event3.setOpen(false);
        event3.setDate(LocalDate.now().plusDays(30));
        event3.setLocation(new LocationDTO("Novi Sad","Serbia","Street","2"));

        GetEventDTO event4 = new GetEventDTO();
        event4.setId(4);
        event4.setOrganizerId(1004);
        event4.setEventTypeId(2004);
        event4.setName("Charity Gala");
        event4.setDescription("A glamorous evening to support a good cause.");
        event4.setMaxParticipants(200);
        event4.setOpen(true);
        event4.setDate(LocalDate.now().plusDays(10));
        event4.setLocation(new LocationDTO("Novi Sad","Serbia","Street","4"));

        GetEventDTO event5 = new GetEventDTO();
        event5.setId(5);
        event5.setOrganizerId(1005);
        event5.setEventTypeId(2005);
        event5.setName("Marathon");
        event5.setDescription("Join the annual marathon and test your endurance.");
        event5.setMaxParticipants(2000);
        event5.setOpen(true);
        event5.setDate(LocalDate.now().plusDays(3));
        event5.setLocation(new LocationDTO("Arilje","Serbia","Street","10"));

        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);

        PagedResponse<GetEventDTO> response = new PagedResponse<>(
                events,
                2,
                5
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
