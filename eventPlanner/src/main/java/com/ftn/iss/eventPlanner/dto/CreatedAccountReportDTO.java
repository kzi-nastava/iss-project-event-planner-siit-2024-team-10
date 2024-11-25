package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatedAccountReportDTO {
    private int id;
    private String description;
    private Status status;
    private int reporterId;
    private int reporteeId;

    public CreatedAccountReportDTO() {
    }
}
