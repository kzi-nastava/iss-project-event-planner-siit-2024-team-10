package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.internal.bytebuddy.dynamic.loading.InjectionClassLoader;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@Table(name = "offerings")
@Getter
@Setter
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
    private Set<Comment> comments = new HashSet<>();
    @ManyToMany
    private Set<Rating> ratings = new HashSet<>();
}
