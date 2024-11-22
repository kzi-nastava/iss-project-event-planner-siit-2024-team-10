package com.ftn.iss.eventPlanner.dto;

import com.ftn.iss.eventPlanner.model.EventType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateProductDTO {
    private String name;
    private String description;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;

    public UpdateProductDTO() {}
}
