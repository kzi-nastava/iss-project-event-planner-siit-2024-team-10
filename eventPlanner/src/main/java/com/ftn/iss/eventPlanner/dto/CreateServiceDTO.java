package com.ftn.iss.eventPlanner.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateServiceDTO {
    private int categoryId;
    private String categoryProposal;
    private boolean pending;
    private int providerID;
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

    private List<String> comments;
    private double averageRating; 

    public CreateServiceDTO() {
    }
}
