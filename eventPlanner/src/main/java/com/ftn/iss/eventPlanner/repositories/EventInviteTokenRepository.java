package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.EventInviteToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventInviteTokenRepository extends JpaRepository<EventInviteToken, Integer> {
    EventInviteToken findByToken(String token);
    List<EventInviteToken> findAllByEventId(int eventId);
}