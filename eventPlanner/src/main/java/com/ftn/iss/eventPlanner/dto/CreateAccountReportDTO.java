package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountReportDTO {
    private String description;
    private String reporterName;
    private String reporteeName;

    public CreateAccountReportDTO() {
    }
}
