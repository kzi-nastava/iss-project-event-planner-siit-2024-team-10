package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAccountReportDTO
{
    private int id;
    private String description;
    private Status status;
    private String reporterEmail;
    private String reporteeEmail;

    public GetAccountReportDTO() {
    }
}
