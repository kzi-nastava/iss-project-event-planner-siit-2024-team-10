package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.accountreport.CreateAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.CreatedAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.GetAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.accountreport.SuspensionStatusDTO;
import com.ftn.iss.eventPlanner.services.AccountReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class AccountReportController {

    @Autowired
    private AccountReportService accountReportService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetAccountReportDTO>> getAllAccountReports() throws Exception{
        List<GetAccountReportDTO> accountReportDTOs = accountReportService.findAllPending();
        return new ResponseEntity<>(accountReportDTOs, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('AUTHENTICATED_USER','EVENT_ORGANIZER','PROVIDER','ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedAccountReportDTO> createAccountReport(@RequestBody CreateAccountReportDTO report) throws Exception {
        CreatedAccountReportDTO createdAccountReportDTO = accountReportService.create(report);
        return new ResponseEntity<>(createdAccountReportDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/suspension-status")
    public ResponseEntity<SuspensionStatusDTO> checkSuspensionDetails(@PathVariable("accountId") int accountId) {
        SuspensionStatusDTO status = accountReportService.getSuspensionDetails(accountId);
        return ResponseEntity.ok(status);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value="/{reportId}/accept", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> acceptReport(@PathVariable int reportId) throws Exception {
        accountReportService.acceptReport(reportId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value="/{reportId}/reject", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rejectReport(@PathVariable int reportId) throws Exception {
        accountReportService.rejectReport(reportId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
