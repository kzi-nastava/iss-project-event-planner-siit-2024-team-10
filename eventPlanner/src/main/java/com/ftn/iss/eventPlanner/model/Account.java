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

    public Account() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(LocalDateTime registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public boolean isNotificationsSilenced() {
        return notificationsSilenced;
    }

    public void setNotificationsSilenced(boolean notificationsSilenced) {
        this.notificationsSilenced = notificationsSilenced;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public List<Event> getFavouriteEvents() {
        return favouriteEvents;
    }

    public void setFavouriteEvents(List<Event> favouriteEvents) {
        this.favouriteEvents = favouriteEvents;
    }

    public List<Event> getAcceptedEvents() {
        return acceptedEvents;
    }

    public void setAcceptedEvents(List<Event> acceptedEvents) {
        this.acceptedEvents = acceptedEvents;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Offering> getFavouriteOfferings() {
        return favouriteOfferings;
    }

    public void setFavouriteOfferings(List<Offering> favouriteOfferings) {
        this.favouriteOfferings = favouriteOfferings;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Account> getBlockedAccounts() {
        return blockedAccounts;
    }

    public void setBlockedAccounts(List<Account> blockedAccounts) {
        this.blockedAccounts = blockedAccounts;
    }
}
