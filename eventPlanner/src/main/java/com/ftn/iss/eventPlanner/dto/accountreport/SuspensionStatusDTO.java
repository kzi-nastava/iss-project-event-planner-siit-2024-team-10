package com.ftn.iss.eventPlanner.dto.accountreport;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SuspensionStatusDTO {
    private boolean suspended;
    private LocalDateTime suspendedUntil;
}
