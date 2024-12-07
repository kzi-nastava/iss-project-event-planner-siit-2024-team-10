package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.event.CreateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.CreatedEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.model.Organizer;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.OrganizerRepository;
import com.ftn.iss.eventPlanner.services.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventCardDTO>> getTopEvents() {
        List<GetEventCardDTO> events = eventService.findTopEvents();
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
        event1.setLocation(new GetLocationDTO("Belgrade","Serbia","Street","2"));

        GetEventDTO event2 = new GetEventDTO();
        event2.setId(2);
        event2.setOrganizerId(1002);
        event2.setEventTypeId(2002);
        event2.setName("Art Exhibition");
        event2.setDescription("Explore stunning artworks by renowned artists.");
        event2.setMaxParticipants(300);
        event2.setOpen(true);
        event2.setDate(LocalDate.now().plusDays(15));
        event2.setLocation(new GetLocationDTO("Belgrade","Serbia","Street","3"));

        GetEventDTO event3 = new GetEventDTO();
        event3.setId(3);
        event3.setOrganizerId(1003);
        event3.setEventTypeId(2002);
        event3.setName("Tech Conference");
        event3.setDescription("Discover the latest trends in technology.");
        event3.setMaxParticipants(1000);
        event3.setOpen(false);
        event3.setDate(LocalDate.now().plusDays(30));
        event3.setLocation(new GetLocationDTO("Novi Sad","Serbia","Street","2"));

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
        event1.setLocation(new GetLocationDTO("Belgrade","Serbia","Street","2"));

        GetEventDTO event2 = new GetEventDTO();
        event2.setId(2);
        event2.setOrganizerId(1002);
        event2.setEventTypeId(2002);
        event2.setName("Art Exhibition");
        event2.setDescription("Explore stunning artworks by renowned artists.");
        event2.setMaxParticipants(300);
        event2.setOpen(true);
        event2.setDate(LocalDate.now().plusDays(15));
        event2.setLocation(new GetLocationDTO("Belgrade","Serbia","Street","3"));

        GetEventDTO event3 = new GetEventDTO();
        event3.setId(3);
        event3.setOrganizerId(1003);
        event3.setEventTypeId(2002);
        event3.setName("Tech Conference");
        event3.setDescription("Discover the latest trends in technology.");
        event3.setMaxParticipants(1000);
        event3.setOpen(false);
        event3.setDate(LocalDate.now().plusDays(30));
        event3.setLocation(new GetLocationDTO("Novi Sad","Serbia","Street","2"));

        GetEventDTO event4 = new GetEventDTO();
        event4.setId(4);
        event4.setOrganizerId(1004);
        event4.setEventTypeId(2004);
        event4.setName("Charity Gala");
        event4.setDescription("A glamorous evening to support a good cause.");
        event4.setMaxParticipants(200);
        event4.setOpen(true);
        event4.setDate(LocalDate.now().plusDays(10));
        event4.setLocation(new GetLocationDTO("Novi Sad","Serbia","Street","4"));

        GetEventDTO event5 = new GetEventDTO();
        event5.setId(5);
        event5.setOrganizerId(1005);
        event5.setEventTypeId(2005);
        event5.setName("Marathon");
        event5.setDescription("Join the annual marathon and test your endurance.");
        event5.setMaxParticipants(2000);
        event5.setOpen(true);
        event5.setDate(LocalDate.now().plusDays(3));
        event5.setLocation(new GetLocationDTO("Arilje","Serbia","Street","10"));

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedEventDTO> createEvent(@Valid @RequestBody CreateEventDTO event) {
        try{
            CreatedEventDTO createdEventType = eventService.create(event);
            return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{eventId}/comments/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedCommentDTO> updateComment(@RequestBody UpdateCommentDTO comment, @PathVariable int eventId, @PathVariable int commentId)
            throws Exception {
        UpdatedCommentDTO updatedComment = new UpdatedCommentDTO();

        updatedComment.setId(commentId);
        updatedComment.setContent(comment.getContent());
        updatedComment.setStatus(comment.getStatus());

        return new ResponseEntity<UpdatedCommentDTO>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{eventId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int eventId, @PathVariable int commentId) throws Exception {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
