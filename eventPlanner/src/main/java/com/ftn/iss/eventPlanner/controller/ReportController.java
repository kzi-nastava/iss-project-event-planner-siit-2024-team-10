package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.GetPricelistItemDTO;
import com.ftn.iss.eventPlanner.services.OfferingService;
import com.ftn.iss.eventPlanner.services.PricelistReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private PricelistReportService reportService;
    @Autowired
    private OfferingService offeringService;

    @GetMapping(value = "/pricelist", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateReport() {
        try {
            List<GetOfferingDTO> pricelistItems = offeringService.findAll();
            List<GetPricelistItemDTO> pricelist = new ArrayList<>();

            for (GetOfferingDTO offering : pricelistItems) {
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

