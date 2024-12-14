package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.GetPricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatePricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatedPricelistItemDTO;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.Service;
import com.ftn.iss.eventPlanner.services.OfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/pricelist")
public class PricelistController {
    @Autowired

    private OfferingService offeringService;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetPricelistItemDTO>> getPricelist() {
        List<GetOfferingDTO> offerings = offeringService.findAll();
        Collection<GetPricelistItemDTO> pricelist = new ArrayList<>();

        for (GetOfferingDTO offering : offerings) {
            GetPricelistItemDTO item = new GetPricelistItemDTO();
            item.setId(offering.getId());
            item.setOfferingId(offering.getId());
            item.setName(offering.getName());
            item.setPrice(offering.getPrice());
            item.setDiscount(offering.getDiscount());
            double priceWithDiscount = offering.getPrice() * (1 - offering.getDiscount()/100);
            item.setPriceWithDiscount(priceWithDiscount);
            pricelist.add(item);
        }
        return ResponseEntity.ok(pricelist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdatedPricelistItemDTO> updatePricing(@PathVariable int id, @RequestBody UpdatePricelistItemDTO updatePricingDTO) {
        Collection<GetPricelistItemDTO> pricelist = fillIn();
        for (GetPricelistItemDTO item : pricelist) {
            if (item.getId() == id) {
                item.setPrice(updatePricingDTO.getPrice());
                item.setDiscount(updatePricingDTO.getDiscount());

                UpdatedPricelistItemDTO response = new UpdatedPricelistItemDTO();
                response.setId(item.getId());
                response.setOfferingId(item.getOfferingId());
                response.setName(item.getName());
                response.setPrice(item.getPrice());
                response.setDiscount(item.getDiscount());

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.notFound().build();
    }
    public Collection<GetPricelistItemDTO> fillIn() {
        Collection<GetPricelistItemDTO> pricelist = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            GetPricelistItemDTO item = new GetPricelistItemDTO();
            item.setId(i);
            item.setOfferingId(i);
            item.setName("Product/Service " + i);
            item.setPrice(100.0 * i);
            item.setDiscount(10.0);
            pricelist.add(item);
        }
        return pricelist;
    }
}
