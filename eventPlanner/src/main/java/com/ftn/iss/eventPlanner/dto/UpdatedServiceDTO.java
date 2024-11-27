package com.ftn.iss.eventPlanner.dto;

import java.util.List;

public class UpdatedServiceDTO {
	private int id;
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
    
    public UpdatedServiceDTO() {
    	
    }
}
