package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.GetPricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatePricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatedPricelistItemDTO;
import com.ftn.iss.eventPlanner.services.OfferingService;
import com.ftn.iss.eventPlanner.services.PricelistReportService;
import com.ftn.iss.eventPlanner.services.PricelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/pricelists")
public class PricelistController {
    @Autowired

    private OfferingService offeringService;
    @Autowired
    private PricelistService pricelistService;
    @Autowired
    private PricelistReportService reportService;
    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @GetMapping(value="/provider/{providerId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetPricelistItemDTO>> getPricelist(@PathVariable int providerId) {
        List<GetOfferingDTO> offerings = offeringService.findAll();
        Collection<GetPricelistItemDTO> pricelist = new ArrayList<>();

        for (GetOfferingDTO offering : offerings) {
            if(offering.getProvider().getId()==providerId){
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
        }
        return ResponseEntity.ok(pricelist);
    }
    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping("/{offeringId}")
    public ResponseEntity<UpdatedPricelistItemDTO> updatePricing(@PathVariable int offeringId, @RequestBody UpdatePricelistItemDTO updatePricingDTO) {
        UpdatedPricelistItemDTO response = pricelistService.updatePricing(offeringId, updatePricingDTO);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateReport() {
        try {
            List<GetPricelistItemDTO> pricelist = reportService.getPricelist();
            byte[] pdfReport = reportService.generateReport(pricelist);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=pricelist_report.pdf")
                    .body(pdfReport);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
