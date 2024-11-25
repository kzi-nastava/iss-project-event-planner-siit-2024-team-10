package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.CreateAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.CreatedAccountReportDTO;
import com.ftn.iss.eventPlanner.dto.GetAccountReportDTO;
import com.ftn.iss.eventPlanner.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/reports")
public class AccountReportController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetAccountReportDTO>> getAccountReports() throws Exception{
        Collection<GetAccountReportDTO> reports = new ArrayList<>();

        GetAccountReportDTO report1 = new GetAccountReportDTO();
        report1.setId(1);
        report1.setStatus(Status.ACCEPTED);
        report1.setDescription("Reason for reporting 1");
        report1.setReporterId(1);
        report1.setReporteeId(2);

        GetAccountReportDTO report2 = new GetAccountReportDTO();
        report2.setId(2);
        report2.setStatus(Status.ACCEPTED);
        report2.setDescription("Reason for reporting 2");
        report2.setReporterId(2);
        report2.setReporteeId(3);

        GetAccountReportDTO report3 = new GetAccountReportDTO();
        report3.setId(3);
        report3.setStatus(Status.ACCEPTED);
        report3.setDescription("Reason for reporting 3");
        report3.setReporterId(4);
        report3.setReporteeId(3);

        reports.add(report1);
        reports.add(report2);
        reports.add(report3);

        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountReportDTO> getAccountReport(@PathVariable("id") int id) throws Exception{

        GetAccountReportDTO report = new GetAccountReportDTO();
        report.setId(id);
        report.setStatus(Status.PENDING);
        report.setDescription("Reason for reporting 1");
        report.setReporterId(1);
        report.setReporteeId(2);

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

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

    @PostMapping("/{id}/suspend")
    public ResponseEntity<String> suspendUser(@PathVariable int id) {
        return ResponseEntity.ok("User suspended successfully");
    }
}
