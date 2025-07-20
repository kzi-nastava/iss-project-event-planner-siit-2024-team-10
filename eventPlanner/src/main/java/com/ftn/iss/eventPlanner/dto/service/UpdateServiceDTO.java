package com.ftn.iss.eventPlanner.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateServiceDTO {
	private String name;
    private String description;
    private String specification;
    private double price;
    private double discount;
    private List<String> photos;
    @JsonProperty("isVisible")
    private boolean isVisible;

    @JsonProperty("isAvailable")
    private boolean isAvailable;
    private int maxDuration;
    private int minDuration;
    private int cancellationPeriod;
    private int reservationPeriod;
    private boolean autoConfirm;
    
    public UpdateServiceDTO() {
    	
    }

}
