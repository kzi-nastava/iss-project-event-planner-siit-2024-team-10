package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetOfferingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getOfferings(
            @RequestParam(required = false) boolean isServiceFilter,
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
            @RequestParam(required = false) boolean isAvailable,
            Pageable pageable
    ){
        Collection<GetOfferingDTO> offerings = new ArrayList<>();

        GetOfferingDTO offering1 = new GetOfferingDTO();
        offering1.setId(1);
        offering1.setName("Test Offering 1");
        offering1.setCategoryId(2);
        offering1.setDescription("Description 1");
        offering1.setPrice(123);
        offering1.setPhotos(Arrays.asList("img.png","img2.png"));
        offering1.setDiscount(20);
        offering1.setRating(2.4);
        offering1.setProviderId(3);

        GetOfferingDTO offering2 = new GetOfferingDTO();
        offering2.setId(2);
        offering2.setName("Test Offering 2");
        offering2.setCategoryId(2);
        offering2.setDescription("Description 2");
        offering2.setPrice(123);
        offering2.setPhotos(Arrays.asList("img3.png","img4.png"));
        offering2.setDiscount(20);
        offering2.setRating(2.4);
        offering2.setProviderId(5);

        GetOfferingDTO offering3 = new GetOfferingDTO();
        offering3.setId(3);
        offering3.setName("Test Offering 3");
        offering3.setCategoryId(3);
        offering3.setDescription("Description 3");
        offering3.setPrice(1223);
        offering3.setPhotos(Arrays.asList("img.png","img2.png"));
        offering3.setDiscount(20);
        offering3.setRating(2.44);
        offering3.setProviderId(3);

        GetOfferingDTO offering4 = new GetOfferingDTO();
        offering4.setId(4);
        offering4.setName("Test Offering 4");
        offering4.setCategoryId(2);
        offering4.setDescription("Description 4");
        offering4.setPrice(123);
        offering4.setPhotos(Arrays.asList("img4.png","img5.png"));
        offering4.setDiscount(20);
        offering4.setRating(2.4);
        offering4.setProviderId(5);

        GetOfferingDTO offering5 = new GetOfferingDTO();
        offering5.setId(5);
        offering5.setName("Test Offering 5");
        offering5.setCategoryId(5);
        offering5.setDescription("Description 5");
        offering5.setPrice(123);
        offering5.setPhotos(Arrays.asList("img.png","img2.png"));
        offering5.setDiscount(20);
        offering5.setRating(2.4);
        offering5.setProviderId(3);

        offerings.add(offering1);
        offerings.add(offering4);
        offerings.add(offering5);
        offerings.add(offering4);
        offerings.add(offering5);

        return new ResponseEntity<Collection<GetOfferingDTO>>(offerings, HttpStatus.OK);
        }
}
