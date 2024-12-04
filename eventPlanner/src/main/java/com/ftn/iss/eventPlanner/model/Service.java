package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Service extends Offering{
    @OneToOne
    private ServiceDetails currentDetails;
    @OneToMany
    private Set<ServiceDetails> detailsHistory = new HashSet<>();

    public Service() {
    }
}
