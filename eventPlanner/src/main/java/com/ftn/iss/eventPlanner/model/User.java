package com.ftn.iss.eventPlanner.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class User {
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePhoto;
    private Location location;
    private Account account;
}
