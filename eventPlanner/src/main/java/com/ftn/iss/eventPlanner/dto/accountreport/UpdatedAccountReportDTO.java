package com.ftn.iss.eventPlanner.dto.accountreport;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdatedAccountReportDTO {
    private int id;
    private String description;
    private String reporterEmail;
    private String reporteeEmail;
    private Status status;
    private LocalDateTime processingTimestamp;

    public UpdatedAccountReportDTO() {
    }
}
