package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountReportDTO {
    private String description;
    private String reporterEmail;
    private String reporteeEmail;

    public CreateAccountReportDTO() {
    }
}
