package com.ftn.iss.eventPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Account implements UserDetails {
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
    private Timestamp lastPasswordResetDate;
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

    public void setPassword(String password) {
        Timestamp now = new Timestamp(new Date().getTime());
        this.setLastPasswordResetDate(now);
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status!=AccountStatus.INACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status!=AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }
}
