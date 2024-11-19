package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Service extends Offering{
    private ServiceDetails currentDetails;
    private List<ServiceDetails> detailsHistory;

    public Service() {
    }
}
