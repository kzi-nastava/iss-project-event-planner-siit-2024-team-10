package com.ftn.iss.eventPlanner.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateServiceDTO {
    @NotNull(message = "name is required")

    private String name;
    @NotNull(message = "description is required")

    private String description;
    @NotNull(message = "specification is required")

    private String specification;
    @NotNull(message = "price is required")

    private double price;
    @NotNull(message = "discount is required")

    private double discount;
    @NotNull(message = "photos is required")

    private List<String> photos;
    @JsonProperty("isVisible")
    @NotNull
    private boolean isVisible;

    @JsonProperty("isAvailable")
    @NotNull
    private boolean isAvailable;
    @NotNull(message = "maxDuration is required")

    private int maxDuration;
    @NotNull(message = "minDuration is required")

    private int minDuration;
    @NotNull(message = "cancellationPeriod is required")

    private int cancellationPeriod;
    @NotNull(message = "reservationPeriod is required")

    private int reservationPeriod;
    @NotNull
    private boolean autoConfirm;
    
    public UpdateServiceDTO() {
    	
    }

}
