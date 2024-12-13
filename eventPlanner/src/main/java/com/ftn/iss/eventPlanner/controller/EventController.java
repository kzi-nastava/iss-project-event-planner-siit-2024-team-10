package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.comment.UpdateCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.UpdatedCommentDTO;
import com.ftn.iss.eventPlanner.dto.event.CreateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.services.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getTopEvents() {
        try {
            List<GetEventDTO> events = eventService.findTopEvents();

            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getEvents(
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String name
    ) {
        try {
            List<GetEventDTO> events = eventService.getAllEvents(
                    eventTypeId, location, maxParticipants, minRating, startDate, endDate, name);

            if (events.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(events);
            }

            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }


    @GetMapping
    public ResponseEntity<PagedResponse<GetEventDTO>> getEvents(
            Pageable pageable,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxParticipants,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String name
    ) {
        try {
            PagedResponse<GetEventDTO> response = eventService.getAllEvents(
                    pageable, eventTypeId, location, maxParticipants, minRating, startDate, endDate, name);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PagedResponse<>(List.of(), 0, 0));
        }
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
