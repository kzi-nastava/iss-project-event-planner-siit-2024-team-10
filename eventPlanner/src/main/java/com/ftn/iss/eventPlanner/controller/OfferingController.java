package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.GetProductDTO;
import com.ftn.iss.eventPlanner.dto.PagedResponse;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
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

    @GetMapping(value="/top", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingDTO>> getTopOfferings(){
        Collection<GetOfferingDTO> offerings = new ArrayList<>();

        GetOfferingDTO offering1 = new GetOfferingDTO();
        offering1.setId(1);
        offering1.setName("Luxury Spa Package");
        offering1.setCategoryId(2);
        offering1.setDescription("A relaxing full-day spa treatment including massages, facials, and body wraps.");
        offering1.setPrice(200);
        offering1.setPhotos(Arrays.asList("https://example.com/photos/spa1.jpg", "https://example.com/photos/spa2.jpg"));
        offering1.setDiscount(15);
        offering1.setRating(4.7);
        offering1.setProviderId(1);

        GetOfferingDTO offering2 = new GetOfferingDTO();
        offering2.setId(2);
        offering2.setName("Gourmet Dinner for Two");
        offering2.setCategoryId(3);
        offering2.setDescription("A romantic dinner with a six-course menu paired with fine wine.");
        offering2.setPrice(150);
        offering2.setPhotos(Arrays.asList("https://example.com/photos/dinner1.jpg", "https://example.com/photos/dinner2.jpg"));
        offering2.setDiscount(10);
        offering2.setRating(4.9);
        offering2.setProviderId(2);

        GetOfferingDTO offering3 = new GetOfferingDTO();
        offering3.setId(3);
        offering3.setName("Mountain Adventure Tour");
        offering3.setCategoryId(4);
        offering3.setDescription("A guided mountain hike with breathtaking views and a picnic lunch.");
        offering3.setPrice(100);
        offering3.setPhotos(Arrays.asList("https://example.com/photos/mountain1.jpg", "https://example.com/photos/mountain2.jpg"));
        offering3.setDiscount(5);
        offering3.setRating(4.5);
        offering3.setProviderId(3);

        GetOfferingDTO offering4 = new GetOfferingDTO();
        offering4.setId(4);
        offering4.setName("Professional Photography Session");
        offering4.setCategoryId(5);
        offering4.setDescription("Capture your special moments with a full-day professional photography session.");
        offering4.setPrice(300);
        offering4.setPhotos(Arrays.asList("https://example.com/photos/photography1.jpg", "https://example.com/photos/photography2.jpg"));
        offering4.setDiscount(20);
        offering4.setRating(5.0);
        offering4.setProviderId(4);

        GetOfferingDTO offering5 = new GetOfferingDTO();
        offering5.setId(5);
        offering5.setName("Private Chef Service");
        offering5.setCategoryId(2);
        offering5.setDescription("Enjoy a private chef cooking a customized meal in your home.");
        offering5.setPrice(250);
        offering5.setPhotos(Arrays.asList("https://example.com/photos/chef1.jpg", "https://example.com/photos/chef2.jpg"));
        offering5.setDiscount(10);
        offering5.setRating(4.8);
        offering5.setProviderId(5);

        offerings.add(offering1);
        offerings.add(offering2);
        offerings.add(offering3);
        offerings.add(offering4);
        offerings.add(offering5);

        return new ResponseEntity<Collection<GetOfferingDTO>>(offerings, HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
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
            @RequestParam(required = false) boolean isAvailable
    ){
        Collection<GetOfferingDTO> offerings = new ArrayList<>();

        GetOfferingDTO offering1 = new GetOfferingDTO();
        offering1.setId(1);
        offering1.setName("Luxury Spa Package");
        offering1.setCategoryId(2);
        offering1.setDescription("A relaxing full-day spa treatment including massages, facials, and body wraps.");
        offering1.setPrice(200);
        offering1.setPhotos(Arrays.asList("https://example.com/photos/spa1.jpg", "https://example.com/photos/spa2.jpg"));
        offering1.setDiscount(15);
        offering1.setRating(4.7);
        offering1.setProviderId(1);

        GetOfferingDTO offering2 = new GetOfferingDTO();
        offering2.setId(2);
        offering2.setName("Gourmet Dinner for Two");
        offering2.setCategoryId(3);
        offering2.setDescription("A romantic dinner with a six-course menu paired with fine wine.");
        offering2.setPrice(150);
        offering2.setPhotos(Arrays.asList("https://example.com/photos/dinner1.jpg", "https://example.com/photos/dinner2.jpg"));
        offering2.setDiscount(10);
        offering2.setRating(4.9);
        offering2.setProviderId(2);

        GetOfferingDTO offering3 = new GetOfferingDTO();
        offering3.setId(3);
        offering3.setName("Mountain Adventure Tour");
        offering3.setCategoryId(4);
        offering3.setDescription("A guided mountain hike with breathtaking views and a picnic lunch.");
        offering3.setPrice(100);
        offering3.setPhotos(Arrays.asList("https://example.com/photos/mountain1.jpg", "https://example.com/photos/mountain2.jpg"));
        offering3.setDiscount(5);
        offering3.setRating(4.5);
        offering3.setProviderId(3);

        GetOfferingDTO offering4 = new GetOfferingDTO();
        offering4.setId(4);
        offering4.setName("Professional Photography Session");
        offering4.setCategoryId(5);
        offering4.setDescription("Capture your special moments with a full-day professional photography session.");
        offering4.setPrice(300);
        offering4.setPhotos(Arrays.asList("https://example.com/photos/photography1.jpg", "https://example.com/photos/photography2.jpg"));
        offering4.setDiscount(20);
        offering4.setRating(5.0);
        offering4.setProviderId(4);

        GetOfferingDTO offering5 = new GetOfferingDTO();
        offering5.setId(5);
        offering5.setName("Private Chef Service");
        offering5.setCategoryId(2);
        offering5.setDescription("Enjoy a private chef cooking a customized meal in your home.");
        offering5.setPrice(250);
        offering5.setPhotos(Arrays.asList("https://example.com/photos/chef1.jpg", "https://example.com/photos/chef2.jpg"));
        offering5.setDiscount(10);
        offering5.setRating(4.8);
        offering5.setProviderId(5);

        offerings.add(offering1);
        offerings.add(offering2);
        offerings.add(offering3);
        offerings.add(offering4);
        offerings.add(offering5);

        return new ResponseEntity<Collection<GetOfferingDTO>>(offerings, HttpStatus.OK);
        }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetOfferingDTO>> getOfferings(
            SpringDataWebProperties.Pageable page,
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
            @RequestParam(required = false) boolean isAvailable
    ){
        Collection<GetOfferingDTO> offerings = new ArrayList<>();

        GetOfferingDTO offering1 = new GetOfferingDTO();
        offering1.setId(1);
        offering1.setName("Luxury Spa Package");
        offering1.setCategoryId(2);
        offering1.setDescription("A relaxing full-day spa treatment including massages, facials, and body wraps.");
        offering1.setPrice(200);
        offering1.setPhotos(Arrays.asList("https://example.com/photos/spa1.jpg", "https://example.com/photos/spa2.jpg"));
        offering1.setDiscount(15);
        offering1.setRating(4.7);
        offering1.setProviderId(1);

        GetOfferingDTO offering2 = new GetOfferingDTO();
        offering2.setId(2);
        offering2.setName("Gourmet Dinner for Two");
        offering2.setCategoryId(3);
        offering2.setDescription("A romantic dinner with a six-course menu paired with fine wine.");
        offering2.setPrice(150);
        offering2.setPhotos(Arrays.asList("https://example.com/photos/dinner1.jpg", "https://example.com/photos/dinner2.jpg"));
        offering2.setDiscount(10);
        offering2.setRating(4.9);
        offering2.setProviderId(2);

        GetOfferingDTO offering3 = new GetOfferingDTO();
        offering3.setId(3);
        offering3.setName("Mountain Adventure Tour");
        offering3.setCategoryId(4);
        offering3.setDescription("A guided mountain hike with breathtaking views and a picnic lunch.");
        offering3.setPrice(100);
        offering3.setPhotos(Arrays.asList("https://example.com/photos/mountain1.jpg", "https://example.com/photos/mountain2.jpg"));
        offering3.setDiscount(5);
        offering3.setRating(4.5);
        offering3.setProviderId(3);

        GetOfferingDTO offering4 = new GetOfferingDTO();
        offering4.setId(4);
        offering4.setName("Professional Photography Session");
        offering4.setCategoryId(5);
        offering4.setDescription("Capture your special moments with a full-day professional photography session.");
        offering4.setPrice(300);
        offering4.setPhotos(Arrays.asList("https://example.com/photos/photography1.jpg", "https://example.com/photos/photography2.jpg"));
        offering4.setDiscount(20);
        offering4.setRating(5.0);
        offering4.setProviderId(4);

        GetOfferingDTO offering5 = new GetOfferingDTO();
        offering5.setId(5);
        offering5.setName("Private Chef Service");
        offering5.setCategoryId(2);
        offering5.setDescription("Enjoy a private chef cooking a customized meal in your home.");
        offering5.setPrice(250);
        offering5.setPhotos(Arrays.asList("https://example.com/photos/chef1.jpg", "https://example.com/photos/chef2.jpg"));
        offering5.setDiscount(10);
        offering5.setRating(4.8);
        offering5.setProviderId(5);

        offerings.add(offering1);
        offerings.add(offering2);
        offerings.add(offering3);
        offerings.add(offering4);
        offerings.add(offering5);

        PagedResponse<GetOfferingDTO> response = new PagedResponse<>(
                offerings,
                1,
                5
        );

        return new ResponseEntity<PagedResponse<GetOfferingDTO>>(response, HttpStatus.OK);
    }
}
