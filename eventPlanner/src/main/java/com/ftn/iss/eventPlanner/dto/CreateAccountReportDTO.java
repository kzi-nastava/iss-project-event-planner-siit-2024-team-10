package com.ftn.iss.eventPlanner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountReportDTO {
    private String description;
    private int reporterId;
    private int reporteeId;

    public CreateAccountReportDTO() {
    }
}
