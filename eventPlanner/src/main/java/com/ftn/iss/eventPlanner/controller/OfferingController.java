package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.comment.*;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingCardDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.rating.CreateRatingDTO;
import com.ftn.iss.eventPlanner.dto.rating.CreatedRatingDTO;
import com.ftn.iss.eventPlanner.dto.rating.GetRatingDTO;
import com.ftn.iss.eventPlanner.model.Status;
import com.ftn.iss.eventPlanner.services.OfferingService;
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

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {

    @Autowired
    private final OfferingService offeringService;

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getTopOfferings() {
        try {
            List<GetOfferingDTO> offerings = offeringService.findTopOfferings();

            return ResponseEntity.ok(offerings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getOfferings(
            @RequestParam(required = false) Boolean isServiceFilter,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Boolean isAvailable
    ){
        try {
            List<GetOfferingDTO> offerings = offeringService.getAllOfferings(
                    isServiceFilter, name, eventTypeId, categoryId, location, minPrice, maxPrice,
                    minDiscount, duration, minRating, startDate, endDate, isAvailable);

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
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Boolean isAvailable
    ){
        try{
            PagedResponse<GetOfferingDTO> offerings = offeringService.getAllOfferings(
                    pageable, isServiceFilter, name, eventTypeId, categoryId, location, minPrice,
                    maxPrice, minDiscount, duration, minRating, startDate, endDate, isAvailable);


            return ResponseEntity.ok(offerings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PagedResponse<>(List.of(), 0, 0));
        }
    }

    @PostMapping(value = "{offeringId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedCommentDTO> createComment(@RequestBody CreateCommentDTO comment) {
        CreatedCommentDTO createdComment = new CreatedCommentDTO();
        createdComment.setId(1);
        createdComment.setContent(comment.getContent());
        createdComment.setStatus(Status.valueOf("PENDING"));
        createdComment.setAccountId(comment.getAccountId());

        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }
    @GetMapping(value = "{offeringId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetCommentDTO>> getComments(@PathVariable("offeringId") int offeringId) {
        Collection<GetCommentDTO> comments = new ArrayList<>();

        GetCommentDTO comment1 = new GetCommentDTO();
        comment1.setId(1);
        comment1.setContent("Great offer!");
        comment1.setStatus(Status.valueOf("ACCEPTED"));
        comment1.setAccountId(45);

        GetCommentDTO comment2 = new GetCommentDTO();
        comment2.setId(2);
        comment2.setContent("Thank you!");
        comment2.setStatus(Status.valueOf("PENDING"));
        comment2.setAccountId(46);

        comments.add(comment1);
        comments.add(comment2);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    @GetMapping(value = "{offeringId}/ratings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetRatingDTO>> getRatings(@PathVariable("offeringId") int offeringId) {
        Collection<GetRatingDTO> ratings = new ArrayList<>();

        GetRatingDTO rating1 = new GetRatingDTO();
        rating1.setId(1);
        rating1.setScore(5);
        rating1.setAccountId(45);

        GetRatingDTO rating2 = new GetRatingDTO();
        rating2.setId(2);
        rating2.setScore(4);
        rating2.setAccountId(46);

        ratings.add(rating1);
        ratings.add(rating2);

        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @PostMapping(value = "{offeringId}/ratings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedRatingDTO> createRating(@RequestBody CreateRatingDTO rating) {
        CreatedRatingDTO createdRating = new CreatedRatingDTO();
        createdRating.setId(1);
        createdRating.setScore(rating.getScore());
        createdRating.setAccountId(rating.getAccoundId());

        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{offeringId}/comments/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedCommentDTO> updateComment(@RequestBody UpdateCommentDTO comment, @PathVariable int offeringId, @PathVariable int commentId)
            throws Exception {
        UpdatedCommentDTO updatedComment = new UpdatedCommentDTO();

        updatedComment.setId(commentId);
        updatedComment.setContent(comment.getContent());
        updatedComment.setStatus(comment.getStatus());

        return new ResponseEntity<UpdatedCommentDTO>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{offeringId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int offeringId, @PathVariable int commentId) throws Exception {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
