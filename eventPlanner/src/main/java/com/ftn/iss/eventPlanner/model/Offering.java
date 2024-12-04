package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import java.util.List;
import org.modelmapper.internal.bytebuddy.dynamic.loading.InjectionClassLoader;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "offerings")
public abstract class Offering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    @ManyToOne
    private OfferingCategory category;
    @Column(nullable = false)
    private boolean pending;
    @Column(nullable = false)
    private boolean isDeleted;
    @Column(nullable = false)
    @ManyToOne
    private Provider provider;
    @Column(nullable = false)
    @ManyToMany
    private List<Comment> comments;
    @Column(nullable = false)
    @ManyToMany
    private List<Rating> ratings;
}
