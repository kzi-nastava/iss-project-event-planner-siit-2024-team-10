package com.ftn.iss.eventPlanner.dto.event;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetGuestsDTO {
    private List<String> guests;
}
