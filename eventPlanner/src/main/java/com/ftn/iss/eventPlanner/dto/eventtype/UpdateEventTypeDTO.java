package com.ftn.iss.eventPlanner.dto.eventtype;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEventTypeDTO {
    @Valid

    @NotNull(message="event type description is required")
    @NotBlank(message="event type description is required")
    private String description;
    private List<Integer> recommendedCategoryIds;
}