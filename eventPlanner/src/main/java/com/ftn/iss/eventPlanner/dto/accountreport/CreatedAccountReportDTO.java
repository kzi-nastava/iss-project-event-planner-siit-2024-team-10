package com.ftn.iss.eventPlanner.dto.accountreport;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedAccountReportDTO {
    private int id;
    private String description;
    private Status status;
    private String reporterEmail;
    private String reporteeEmail;

    public CreatedAccountReportDTO() {
    }
}
