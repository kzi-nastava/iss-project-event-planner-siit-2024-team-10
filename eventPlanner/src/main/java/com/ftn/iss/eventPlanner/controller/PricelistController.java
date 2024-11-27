package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/api/pricelist")
public class PricelistController {

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetPricelistItemDTO>> getPricelist() {
        Collection<GetPricelistItemDTO> pricelist = fillIn();
        return ResponseEntity.ok(pricelist);
    }
    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetPricelistItemDTO>> getProductsPage(
            SpringDataWebProperties.Pageable page,
            @RequestParam(required = false) Double discount,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String name
    ) {
        Collection<GetPricelistItemDTO> pricelist = fillIn();

        PagedResponse<GetPricelistItemDTO> response = new PagedResponse<>(
                pricelist,
                1,
                5
        );

        return new ResponseEntity<PagedResponse<GetPricelistItemDTO>>(response, HttpStatus.OK);
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
