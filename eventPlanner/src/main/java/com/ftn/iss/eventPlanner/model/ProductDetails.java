package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class ProductDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private double price;
    @Column
    private double discount;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "offering_details_photos", joinColumns = @JoinColumn(name = "offerin_details_id"))
    @Column(name = "photo_url")
    private List<String> photos;
    @Column(nullable = false)
    private boolean isVisible;
    @Column(nullable = false)
    private boolean isAvailable;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    public ProductDetails() {
    }

}
