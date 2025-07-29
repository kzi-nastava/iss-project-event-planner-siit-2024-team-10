package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.agendaitem.*;
import com.ftn.iss.eventPlanner.dto.budgetitem.*;
import com.ftn.iss.eventPlanner.dto.event.*;
import com.ftn.iss.eventPlanner.dto.eventstats.GetEventStatsDTO;
import com.ftn.iss.eventPlanner.services.BudgetItemService;
import com.ftn.iss.eventPlanner.services.EventService;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private BudgetItemService budgetItemService;

    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getTopEvents(
            @RequestParam(required = false) Integer accountId) {
        List<GetEventDTO> events = eventService.findTopEvents(accountId);
        return new ResponseEntity<>(events, HttpStatus.OK);
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
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) Boolean initLoad
    ) {
        PagedResponse<GetEventDTO> response = eventService.getAllEvents(
                pageable, eventTypeId, location, maxParticipants, minRating, startDate, endDate, name, sortBy, sortDirection, accountId, initLoad);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value="/organizers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetEventDTO>> findEventsByOrganizer(@RequestParam Integer accountId){
        List<GetEventDTO> events = eventService.findEventsByOrganizer(accountId);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedEventDTO> createEvent(@Valid @RequestBody CreateEventDTO event) {
        CreatedEventDTO createdEventType = eventService.create(event);
        return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PutMapping(value = "/{eventId}", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedEventDTO> updateEvent(@Valid @RequestBody UpdateEventDTO event, @PathVariable int eventId) {
        UpdatedEventDTO createdEventType = eventService.update(eventId, event);
        return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @DeleteMapping(value = "/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable int eventId) {
        eventService.delete(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{eventId}/agenda", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetAgendaItemDTO>> getEventAgenda(@PathVariable int eventId) {
        Collection<GetAgendaItemDTO> agendaItems = eventService.getAgenda(eventId);
        return new ResponseEntity<>(agendaItems,HttpStatus.CREATED);
    }

    @GetMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetEventDTO> getEvent(@PathVariable int eventId) {
        GetEventDTO event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping(value="/{eventId}/ratings", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedEventRatingDTO> rateEvent(@PathVariable int eventId, @Valid @RequestBody CreateEventRatingDTO rating) {
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
    public ResponseEntity<UpdatedAgendaItemDTO> updateAgendaItem(@PathVariable int eventId, @PathVariable int agendaItemId, @Valid @RequestBody UpdateAgendaItemDTO agendaItemDto) {
        UpdatedAgendaItemDTO updatedAgendaItemDTO = eventService.updateAgendaItem(eventId, agendaItemId, agendaItemDto);
        return ResponseEntity.ok(updatedAgendaItemDTO);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @DeleteMapping(value="/{eventId}/agenda/{agendaItemId}")
    public ResponseEntity<Void> deleteAgendaItem(@PathVariable int eventId, @PathVariable int agendaItemId) {
        eventService.deleteAgendaItem(eventId, agendaItemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','ADMIN')")
    @GetMapping(value="/{eventId}/reports/open-event", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getOpenEventReport(@PathVariable int eventId) throws JRException {
        byte[] pdfReport= eventService.generateOpenEventReport(eventId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=event_report.pdf")
                .body(pdfReport);
    }

    @GetMapping(value="/{eventId}/reports/info", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getEventReport(@PathVariable int eventId) throws JRException {
        byte[] pdfReport= eventService.generateEventInfoReport(eventId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=event_report.pdf")
                .body(pdfReport);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @GetMapping(value="/{eventId}/reports/guestlist", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getGuestlistReport(@PathVariable int eventId) throws JRException {
        byte[] pdfReport= eventService.generateGuestlistReport(eventId);
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
    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(value="/{eventId}/budget", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedBudgetItemDTO> createBudgetItem(@PathVariable int eventId, @Valid @RequestBody CreateBudgetItemDTO createBudgetItemDTO) {
        CreatedBudgetItemDTO createdBudgetItemDTO = budgetItemService.create(eventId, createBudgetItemDTO,0);
        return new ResponseEntity<>(createdBudgetItemDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PutMapping(value="/{eventId}/budget/{budgetItemId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedBudgetItemDTO> updateBudgetItemAmount(@PathVariable int eventId, @PathVariable int budgetItemId, @RequestBody UpdateBudgetItemDTO amount) {
        UpdatedBudgetItemDTO updatedBudgetItemDTO = budgetItemService.updateAmount(budgetItemId, amount);
        return ResponseEntity.ok(updatedBudgetItemDTO);
    }
    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PutMapping(value = "/{eventId}/budget/buy/{offeringId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> buyOfferingForEvent(
            @PathVariable int eventId,
            @PathVariable int offeringId) {
        budgetItemService.buy(eventId, offeringId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @DeleteMapping(value="/{eventId}/budget/{budgetItemId}")
    public ResponseEntity<Void> deleteBudgetItem(@PathVariable int eventId, @PathVariable int budgetItemId) {
        budgetItemService.delete(eventId, budgetItemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @GetMapping(value = "/budget/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetBudgetItemDTO>> getBudgetItemsByEvent(@PathVariable int eventId) {
        List<GetBudgetItemDTO> items = budgetItemService.findByEventId(eventId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/budget/total")
    public ResponseEntity<Double> getTotalBudget(@PathVariable int eventId) {
        double total = budgetItemService.getTotalBudgetForEvent(eventId);
        return ResponseEntity.ok(total);
    }
    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @GetMapping(value = "/{eventId}/guests")
    public ResponseEntity<GetGuestsDTO> getGuests(@PathVariable int eventId){
        GetGuestsDTO guests = eventService.getGuestList(eventId);
        return ResponseEntity.ok(guests);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(value="/{eventId}/invite")
    public ResponseEntity<?> sendInvitations(@PathVariable int eventId, @Valid @RequestBody CreateGuestListDTO guests) {
        eventService.sendInvitations(eventId, guests);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accept-invite/{token}")
    public ResponseEntity<Void> redirectToClient(@PathVariable String token, @RequestHeader("User-Agent") String userAgent) {
        String redirectUrl;
        if (userAgent.toLowerCase().contains("android")) {
            redirectUrl = "m3.eventplanner://accept-invite?invitation-token=" + token;
        } else {
            redirectUrl = "http://localhost:4200/accept-invite?invitation-token=" + token;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/process-invitation")
    public ResponseEntity<Void> processInvitation(@RequestParam("invitation-token") String token, @Valid @RequestBody GetGuestDTO guest) {
        eventService.processInvitation(token, guest);
        return ResponseEntity.ok().build();
    }

}
