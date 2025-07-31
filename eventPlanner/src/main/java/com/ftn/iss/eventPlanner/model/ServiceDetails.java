package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class ServiceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String specification;
    @Column(nullable = false)
    private double price;
    @Column
    private double discount;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "offering_details_photos", joinColumns = @JoinColumn(name = "offering_details_id"))
    @Column(name = "photo_url")
    private List<String> photos;
    @Column(nullable = false)
    private boolean fixedTime;
    @Column(nullable = false)
    private int maxDuration;
    @Column(nullable = false)
    private int minDuration;
    @Column(nullable = false)
    private int cancellationPeriod;
    @Column(nullable = false)
    private int reservationPeriod;
    @Column(nullable = false)
    private boolean isVisible;
    @Column(nullable = false)
    private boolean isAvailable;
    @Column(nullable = false)
    private boolean autoConfirm;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ServiceDetails() {
    }
}