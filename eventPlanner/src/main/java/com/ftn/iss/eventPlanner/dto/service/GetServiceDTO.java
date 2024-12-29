package com.ftn.iss.eventPlanner.dto.service;

import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetServiceDTO {
	private int id;
    private GetOfferingCategoryDTO category;
    private boolean pending;
    private GetProviderDTO provider;
    private String name;
    private String description;
    private String specification;
    private double price;
    private double discount;
    private List<String> photos;
    private boolean isVisible;
    private boolean isAvailable;
    private int maxDuration;
    private int minDuration;
    private int cancellationPeriod;
    private int reservationPeriod;
    private boolean autoConfirm;
    private boolean deleted;

    public GetServiceDTO() {
    	
    }

}
