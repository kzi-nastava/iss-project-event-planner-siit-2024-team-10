package com.ftn.iss.eventPlanner.dto.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateServiceDTO {
    @Valid

    @NotNull
    private int category;
    @NotNull
    private String categoryProposal;

    @NotNull
    private boolean pending;

    @NotNull
    private int provider;

    @NotNull
    private String name;

    @NotNull
    private String description;
    @NotNull

    private String specification;

    @NotNull
    private double price;

    @NotNull
    private double discount;

    @NotNull
    private List<String> photos;
    @NotNull
    private boolean isVisible;
    @NotNull
    private boolean isAvailable;

    @NotNull
    private int maxDuration;

    @NotNull
    private int minDuration;

    @NotNull
    private int cancellationPeriod;

    @NotNull
    private int reservationPeriod;
    @NotNull
    private boolean autoConfirm;

    public CreateServiceDTO() {
    }
}
