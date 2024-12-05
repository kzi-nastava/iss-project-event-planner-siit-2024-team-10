package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class OfferingDetails {
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
    @CollectionTable(name = "offering_details_photos", joinColumns = @JoinColumn(name = "offerin_details_id"))
    @Column(name = "photo_url")
    private Set<String> photos = new HashSet<>();
    @Column
    private boolean fixedTime;
    @Column
    private int maxDuration;
    @Column
    private int minDuration;
    @Column
    private int cancellationPeriod;
    @Column
    private int reservationPeriod;
    @Column(nullable = false)
    private boolean isVisible;
    @Column(nullable = false)
    private boolean isAvailable;
    @Column(nullable = false)
    private boolean autoConfirm;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public OfferingDetails() {
    }

}
