package com.ftn.iss.eventPlanner.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private boolean notificationsSilenced;
    @Column(nullable = false)
    private Timestamp lastPasswordResetDate;
    @Column(nullable = false)
    @Enumerated
    private AccountStatus status;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Event> favouriteEvents = new HashSet<>();
    @ManyToMany
    private Set<Event> acceptedEvents = new HashSet<>();;
    @OneToOne
    private User user;
    @ManyToMany(fetch = FetchType.LAZY)

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
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return status!=AccountStatus.INACTIVE;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return status!=AccountStatus.SUSPENDED;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }
}
