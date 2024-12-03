package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String description;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_photos", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "photo_url")
    private List<String> photos;
    @ManyToOne
    private Location location;

    public Company() {
    }
}
