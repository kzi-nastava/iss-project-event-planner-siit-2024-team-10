package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.accountreport.CreateAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.CreatedAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.GetAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.UpdatedAccountReportDTO;
import com.ftn.iss.eventPlanner.services.AccountReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/reports")
public class AccountReportController {

    @Autowired
    private AccountReportService accountReportService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetAccountReportDTO>> getAllAccountReports() {
        List<GetAccountReportDTO> accountReportDTOs = accountReportService.findAllPending();
        return new ResponseEntity<>(accountReportDTOs, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('AUTHENTICATED_USER','EVENT_ORGANIZER','PROVIDER','ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedAccountReportDTO> createAccountReport(@Valid @RequestBody CreateAccountReportDTO report) {
        CreatedAccountReportDTO createdAccountReportDTO = accountReportService.create(report);
        return new ResponseEntity<>(createdAccountReportDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value="/{reportId}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedAccountReportDTO> acceptReport(@PathVariable int reportId) {
        UpdatedAccountReportDTO dto = accountReportService.acceptReport(reportId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value="/{reportId}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedAccountReportDTO> rejectReport(@PathVariable int reportId) {
        UpdatedAccountReportDTO dto = accountReportService.rejectReport(reportId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
