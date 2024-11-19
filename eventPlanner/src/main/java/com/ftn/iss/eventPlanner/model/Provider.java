package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Provider extends User {
    private Company company;

    public Provider() {
    }
}
