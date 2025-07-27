package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.comment.*;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.ChangeOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.services.CommentService;
import com.ftn.iss.eventPlanner.services.OfferingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api/offerings")
public class OfferingController {

    @Autowired
    private final OfferingService offeringService;
    @Autowired
    private CommentService commentService;

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getTopOfferings(@RequestParam(required = false) Integer accountId) {
        List<GetOfferingDTO> offerings = offeringService.findTopOfferings(accountId);
        return new ResponseEntity<>(offerings, HttpStatus.OK);
    }
    @GetMapping(value="/{providerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getProvidersOfferings(@PathVariable int providerId) {
        List<GetOfferingDTO> offerings = offeringService.findProvidersOfferings(providerId);
        return ResponseEntity.ok(offerings);
    }
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getOfferings(
            @RequestParam(required = false) Boolean isServiceFilter,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) Integer serviceDuration,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean isAvailable
    ){
        List<GetOfferingDTO> offerings = offeringService.getAllOfferings(
                isServiceFilter, name, categoryId, location, minPrice, maxPrice,
                minDiscount, serviceDuration, minRating, isAvailable);

        return ResponseEntity.ok(offerings);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetOfferingDTO>> getOfferings(
            Pageable pageable,
            @RequestParam(required = false) Boolean isServiceFilter,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double startPrice,
            @RequestParam(required = false) Double endPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) Integer serviceDuration,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) Integer providerId,
            @RequestParam(required = false) Boolean initLoad

    ){
        PagedResponse<GetOfferingDTO> offerings = offeringService.getAllOfferings(
                pageable, isServiceFilter, name, categoryId, location, startPrice,
                endPrice, minDiscount, serviceDuration, minRating, isAvailable, sortBy, sortDirection, accountId, providerId, initLoad);

        return new ResponseEntity<>(offerings, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(value = "{offeringId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedCommentDTO> createComment(@PathVariable int offeringId,@Valid @RequestBody CreateCommentDTO comment) {
        CreatedCommentDTO createdEventType = commentService.create(comment,offeringId);
        return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);
    }
    @GetMapping(value = "{offeringId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetCommentDTO>> getComments(@PathVariable("offeringId") int offeringId) {
        Collection<GetCommentDTO> comments = offeringService.getComments(offeringId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping (value = "/comments/{commentId}/reject",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rejectComment(@PathVariable int commentId) throws Exception {
        commentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value="/comments/{commentId}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> approveComment(@PathVariable int commentId) throws Exception {
        commentService.approve(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(value="/comments/pending")
    public ResponseEntity<Collection<GetCommentDTO>> getPendingComments(){
        Collection<GetCommentDTO> comments = commentService.getPendingComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping(value = "/highest-prices")
    public ResponseEntity<?> getHighestPrice(@RequestParam(required = false) Boolean isService) {
        Double highestPrice = offeringService.getHighestPrice(isService);
        return new ResponseEntity<>(highestPrice, HttpStatus.OK);
    }

    @PutMapping("/{offeringId}/category")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> changeOfferingCategory(@PathVariable int offeringId,@Valid @RequestBody ChangeOfferingCategoryDTO dto) {
        offeringService.changeCategory(offeringId, dto.getCategoryId());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value="/all-non-paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getOfferings() {
        List<GetOfferingDTO> offerings = offeringService.findAll();
        return ResponseEntity.ok(offerings);
    }
}