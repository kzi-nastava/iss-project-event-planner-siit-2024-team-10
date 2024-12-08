package com.ftn.iss.eventPlanner.dto.offering;

import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GetOfferingCardDTO {
    private int id;
    private String provider;
    private String category;
    private Boolean isService;
    private String name;
    private double price;
    private String coverPicture;
    private double averageRating;
}
