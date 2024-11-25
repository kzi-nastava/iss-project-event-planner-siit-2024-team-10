package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.GetProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getOfferings(){
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
        offering3.setId(1);
        offering3.setName("Test Offering 1");
        offering3.setCategoryId(2);
        offering3.setDescription("Description 1");
        offering3.setPrice(123);
        offering3.setPhotos(Arrays.asList("img.png","img2.png"));
        offering3.setDiscount(20);
        offering3.setRating(2.4);
        offering3.setProviderId(3);

        offerings.add(offering1);
        offerings.add(offering2);
        offerings.add(offering3);

        return new ResponseEntity<Collection<GetOfferingDTO>>(offerings, HttpStatus.OK);
        }
}
