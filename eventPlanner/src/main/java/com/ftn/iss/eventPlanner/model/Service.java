package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Service extends Offering {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_service_details_id")
    private ServiceDetails currentDetails;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ServiceDetails> serviceDetailsHistory = new HashSet<>();
}
