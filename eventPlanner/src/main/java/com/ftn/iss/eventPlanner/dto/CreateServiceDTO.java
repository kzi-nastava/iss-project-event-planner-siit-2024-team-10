package com.ftn.iss.eventPlanner.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateServiceDTO {
    private int id;
    private String category;
    private boolean pending;
    private boolean isDeleted;
    private String providerName;
    private List<String> comments;
    private double averageRating; 
    private CreateServiceDetailsDTO currentDetails;
    private List<CreateServiceDetailsDTO> detailsHistory;

    public CreateServiceDTO() {
    }
}
