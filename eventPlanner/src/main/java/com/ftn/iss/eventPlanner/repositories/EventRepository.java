package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Integer> {
}
