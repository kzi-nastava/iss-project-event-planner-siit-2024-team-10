package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated
    private Role role;
    @Column(nullable = false)
    private LocalDateTime registrationTimestamp;
    @Column(nullable = false)
    private boolean notificationsSilenced;
    @Column(nullable = false)
    @Enumerated
    private AccountStatus status;
    @ManyToMany
    private Set<Event> favouriteEvents = new HashSet<>();
    @ManyToMany
    private Set<Event> acceptedEvents = new HashSet<>();;
    @OneToOne
    private User user;
    @ManyToMany
    private Set<Offering> favouriteOfferings = new HashSet<>();;
    @OneToMany
    private Set<Notification> notifications = new HashSet<>();;
    @ManyToMany
    private Set<Account> blockedAccounts = new HashSet<>();;

    public Account() {
    }
}
