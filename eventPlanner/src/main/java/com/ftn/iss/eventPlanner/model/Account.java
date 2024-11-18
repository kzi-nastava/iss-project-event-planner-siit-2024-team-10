package com.ftn.iss.eventPlanner.model;

import java.time.LocalDateTime;
import java.util.List;

public class Account {
    private int id;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime registrationTimestamp;
    private boolean notificationsSilenced;
    private AccountStatus status;
    private List<Event> favouriteEvents;
    private List<Event> acceptedEvents;
    private User user;
    private List<Offering> favouriteOfferings;
    private List<Notification> notifications;
    private List<Account> blockedAccounts;
}
