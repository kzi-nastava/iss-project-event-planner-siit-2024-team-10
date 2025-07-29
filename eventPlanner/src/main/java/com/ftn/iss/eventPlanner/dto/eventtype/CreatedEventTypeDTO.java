package com.ftn.iss.eventPlanner.dto.eventtype;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatedEventTypeDTO {
    private int id;
    private String name;
    private String description;
}
