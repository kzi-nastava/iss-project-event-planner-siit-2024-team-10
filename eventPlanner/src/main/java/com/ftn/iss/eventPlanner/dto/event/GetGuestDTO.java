package com.ftn.iss.eventPlanner.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetGuestDTO {
    @NotNull(message = "Guest email is required")
    private String email;
}
