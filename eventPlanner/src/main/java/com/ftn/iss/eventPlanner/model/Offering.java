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
    @ManyToOne
    private OfferingCategory category;
    @Column(nullable = false)
    private boolean pending;
    @Column(nullable = false)
    private boolean isDeleted;
    @ManyToOne
    private Provider provider;
    @ManyToMany
    private List<Comment> comments;
    @ManyToMany
    private List<Rating> ratings;
}
