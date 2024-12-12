package com.ftn.iss.eventPlanner.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateEventDTO {
    @Valid

    @NotNull
    private int eventTypeId;
    @NotNull
    private int organizerId;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private int maxParticipants;
    @NotNull
    @JsonProperty("isOpen")
    private boolean isOpen;
    @NotNull
    private LocalDate date;
    @NotNull
    private CreateLocationDTO location;
}
