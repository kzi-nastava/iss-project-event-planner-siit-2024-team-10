package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedAccountReportDTO {
    private int id;
    private String description;
    private String reporterUsername;
    private String reporteeUsername;

    public CreatedAccountReportDTO() {
    }
}
