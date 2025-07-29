package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Provider extends User {
    @OneToOne
    private Company company;

    public Provider() {
    }
}
