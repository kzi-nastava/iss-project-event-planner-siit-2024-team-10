package com.ftn.iss.eventPlanner.dto.accountreport;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountReportDTO {

    @NotNull(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String description;

    @NotNull(message = "Reporter ID is required")
    @Min(value = 1, message = "Reporter ID must be a positive number")
    private Integer reporterId;

    @NotNull(message = "Reportee ID is required")
    @Min(value = 1, message = "Reportee ID must be a positive number")
    private Integer reporteeId;

    public CreateAccountReportDTO() {
    }
}
