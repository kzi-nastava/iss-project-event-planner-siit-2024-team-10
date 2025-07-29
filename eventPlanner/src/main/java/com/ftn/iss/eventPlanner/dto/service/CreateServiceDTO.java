package com.ftn.iss.eventPlanner.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateServiceDTO {
    @NotNull
    private int categoryId;

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
    @JsonProperty("isAvailable")
    private boolean isAvailable;

    @JsonProperty("isVisible")
    private boolean isVisible;
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
    private String categoryProposalName;
    private String categoryProposalDescription;

    public CreateServiceDTO() {
    }
}