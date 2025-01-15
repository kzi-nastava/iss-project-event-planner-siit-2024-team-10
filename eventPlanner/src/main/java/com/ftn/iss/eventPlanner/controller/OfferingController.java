package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.comment.*;
import com.ftn.iss.eventPlanner.dto.eventtype.CreatedEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.rating.CreateRatingDTO;
import com.ftn.iss.eventPlanner.dto.rating.CreatedRatingDTO;
import com.ftn.iss.eventPlanner.dto.rating.GetRatingDTO;
import com.ftn.iss.eventPlanner.model.Status;
import com.ftn.iss.eventPlanner.services.CommentService;
import com.ftn.iss.eventPlanner.services.OfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

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
    public ResponseEntity<Collection<GetOfferingDTO>> getTopOfferings(
            @RequestParam(required = false) Integer accountId
    ) {
        try {
            List<GetOfferingDTO> offerings = offeringService.findTopOfferings(accountId);

            return ResponseEntity.ok(offerings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
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
        try {
            List<GetOfferingDTO> offerings = offeringService.getAllOfferings(
                    isServiceFilter, name, categoryId, location, minPrice, maxPrice,
                    minDiscount, serviceDuration, minRating, isAvailable);

            return ResponseEntity.ok(offerings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
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
            @RequestParam(required = false) Integer accountId

    ){
        try{
            PagedResponse<GetOfferingDTO> offerings = offeringService.getAllOfferings(
                    pageable, isServiceFilter, name, categoryId, location, startPrice,
                    endPrice, minDiscount, serviceDuration, minRating, isAvailable, sortBy, sortDirection,accountId);


            return ResponseEntity.ok(offerings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PagedResponse<>(List.of(), 0, 0));
        }
    }

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER')")
    @PostMapping(value = "{offeringId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedCommentDTO> createComment(@PathVariable int offeringId, @RequestBody CreateCommentDTO comment) {
        try{
            CreatedCommentDTO createdEventType = commentService.create(comment,offeringId);
            return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value = "{offeringId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetCommentDTO>> getComments(@PathVariable("offeringId") int offeringId) {
        Collection<GetCommentDTO> comments = offeringService.getComments(offeringId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping(value = "/{offeringId}/comments/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedCommentDTO> updateComment(@RequestBody UpdateCommentDTO comment, @PathVariable int offeringId, @PathVariable int commentId)
            throws Exception {
        UpdatedCommentDTO updatedComment = new UpdatedCommentDTO();

        updatedComment.setId(commentId);
        updatedComment.setContent(comment.getContent());

        return new ResponseEntity<UpdatedCommentDTO>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{offeringId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int offeringId, @PathVariable int commentId) throws Exception {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/highest-prices")
    public ResponseEntity<?> getHighestPrice(@RequestParam(required = false) Boolean isService) {
        try {
            Double highestPrice = offeringService.getHighestPrice(isService);
            return ResponseEntity.ok(highestPrice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    @GetMapping(value = "/provider/{providerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getOfferingsByProviderId(@PathVariable int providerId) {
        try {
            List<GetOfferingDTO> offerings = offeringService.getOfferingsByProviderId(providerId);
            return ResponseEntity.ok(offerings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

}
