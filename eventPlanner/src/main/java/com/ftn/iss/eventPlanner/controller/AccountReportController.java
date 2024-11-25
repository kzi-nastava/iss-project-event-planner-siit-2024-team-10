package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.CreateAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.CreatedAccountReportDTO;
import com.ftn.iss.eventPlanner.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class AccountReportController {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedAccountReportDTO> createAccountReport(@RequestBody CreateAccountReportDTO report) throws Exception {
        CreatedAccountReportDTO createdReport = new CreatedAccountReportDTO();
        createdReport.setId(1);
        createdReport.setStatus(Status.PENDING);
        createdReport.setDescription(report.getDescription());
        createdReport.setReporterId(report.getReporterId());
        createdReport.setReporterId(report.getReporterId());

        return new ResponseEntity<CreatedAccountReportDTO>(createdReport, HttpStatus.CREATED);
    }
}
