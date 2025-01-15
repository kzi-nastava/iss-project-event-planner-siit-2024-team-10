package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.agendaitem.*;
import com.ftn.iss.eventPlanner.dto.comment.UpdateCommentDTO;
import com.ftn.iss.eventPlanner.dto.comment.UpdatedCommentDTO;
import com.ftn.iss.eventPlanner.dto.event.*;
import com.ftn.iss.eventPlanner.dto.eventstats.GetEventStatsDTO;
import com.ftn.iss.eventPlanner.model.EventStats;
import com.ftn.iss.eventPlanner.services.EventService;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getTopEvents(
            @RequestParam(required = false) Integer accountId
            ) {
        try {
            List<GetEventDTO> events = eventService.findTopEvents(accountId);

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
            @RequestParam(required = false) @DateTimeFormat(pattern="MM/dd/yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern="MM/dd/yyyy") LocalDate endDate,
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
            @RequestParam(required = false)  @DateTimeFormat(pattern="MM/dd/yyyy") LocalDate startDate,
            @RequestParam(required = false)  @DateTimeFormat(pattern="MM/dd/yyyy") LocalDate endDate,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer accountId
    ) {
        try {
            PagedResponse<GetEventDTO> response = eventService.getAllEvents(
                    pageable, eventTypeId, location, maxParticipants, minRating, startDate, endDate, name, sortBy, sortDirection, accountId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PagedResponse<>(List.of(), 0, 0));
        }
    }




    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
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

        return new ResponseEntity<UpdatedCommentDTO>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{eventId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int eventId, @PathVariable int commentId) throws Exception {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{eventId}/agenda", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetAgendaItemDTO>> getEventAgenda(@PathVariable int eventId) {
        Collection<GetAgendaItemDTO> agendaItems = eventService.getAgenda(eventId);
        return ResponseEntity.ok(agendaItems);
    }

    @GetMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetEventDTO> getEvent(@PathVariable int eventId) {
        GetEventDTO event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping(value="/{eventId}/ratings", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedEventRatingDTO> rateEvent(@PathVariable int eventId, @RequestBody CreateEventRatingDTO rating) {
        CreatedEventRatingDTO ratedEvent = eventService.rateEvent(eventId, rating.getRating());
        return ResponseEntity.ok(ratedEvent);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(value="/{eventId}/agenda", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedAgendaItemDTO> createAgendaItem(@PathVariable int eventId,@Valid @RequestBody CreateAgendaItemDTO agendaItemDto) {
        CreatedAgendaItemDTO createdAgendaItemDTO = eventService.createAgendaItem(eventId, agendaItemDto);
        return ResponseEntity.ok(createdAgendaItemDTO);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PutMapping(value="/{eventId}/agenda/{agendaItemId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedAgendaItemDTO> updateAgendaItem(@PathVariable int eventId, @PathVariable int agendaItemId, @RequestBody UpdateAgendaItemDTO agendaItemDto) {
        UpdatedAgendaItemDTO updatedAgendaItemDTO = eventService.updateAgendaItem(eventId, agendaItemId, agendaItemDto);
        return ResponseEntity.ok(updatedAgendaItemDTO);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @DeleteMapping(value="/{eventId}/agenda/{agendaItemId}")
    public ResponseEntity<Void> deleteAgendaItem(@PathVariable int eventId, @Valid @PathVariable int agendaItemId) {
        eventService.deleteAgendaItem(eventId, agendaItemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','ADMIN')")
    @GetMapping(value="/{eventId}/reports/open-event", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getEventReport(@PathVariable int eventId) throws JRException {
        byte[] pdfReport= eventService.generateOpenEventReport(eventId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=event_report.pdf")
                .body(pdfReport);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','ADMIN')")
    @GetMapping(value="/{eventId}/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetEventStatsDTO> getEventStats(@PathVariable int eventId) {
        GetEventStatsDTO eventStats = eventService.getEventStats(eventId);
        return ResponseEntity.ok(eventStats);
    }

    @PostMapping(value="/{eventId}/stats/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetEventStatsDTO> addParticipant(@PathVariable int eventId) {
        GetEventStatsDTO updatedEventStats = eventService.addParticipant(eventId);
        return ResponseEntity.ok(updatedEventStats);
    }
}
