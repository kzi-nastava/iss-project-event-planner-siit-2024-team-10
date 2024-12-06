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
public class UpdatedEventTypeDTO {
    private int id;
    private String name;
    private String description;
    private List<Integer> recommendedCategoryIds;
}