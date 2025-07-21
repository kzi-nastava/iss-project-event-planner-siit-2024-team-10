package com.ftn.iss.eventPlanner.dto.accountreport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountReportDTO {
    private String description;
    private Integer reporterId;
    private Integer reporteeId;

    public CreateAccountReportDTO() {
    }
}
