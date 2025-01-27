package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.event.AddFavouriteEventDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@CrossOrigin
@RequestMapping(value = "api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @GetMapping(value="/{accountId}/favourite-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetEventDTO>> getFavouriteEvents(@PathVariable int accountId) {
        Collection<GetEventDTO> favouriteEvents = accountService.getFavouriteEvents(accountId);
        return ResponseEntity.ok(favouriteEvents);
    }
    @PostMapping(value="/{accountId}/favourite-events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addEventToFavourites(@PathVariable int accountId, @RequestBody AddFavouriteEventDTO addFavouriteEventDTO) {
        accountService.addEventToFavourites(accountId, addFavouriteEventDTO.getEventId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(value="/{accountId}/favourite-events/{eventId}")
    public ResponseEntity<?> removeEventFromFavourites(@PathVariable int accountId, @PathVariable int eventId) {
        accountService.removeEventFromFavourites(accountId, eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping(value="/{accountId}/favourite-offerings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getFavouriteOfferings(@PathVariable int accountId) {
        Collection<GetOfferingDTO> favouriteEvents = accountService.getFavouriteOfferings(accountId);
        return ResponseEntity.ok(favouriteEvents);
    }
    @PostMapping(value="/{accountId}/favourite-offerings/{offeringId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addOfferingToFavourites(@PathVariable int accountId, @PathVariable int offeringId) {
        accountService.addOfferingToFavourites(accountId, offeringId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(value="/{accountId}/favourite-offerings/{offeringId}")
    public ResponseEntity<?> removeOfferingFromFavourites(@PathVariable int accountId, @PathVariable int offeringId) {
        accountService.removeOfferingFromFavourites(accountId, offeringId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}