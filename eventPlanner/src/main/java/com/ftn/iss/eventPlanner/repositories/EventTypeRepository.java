package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTypeRepository extends JpaRepository<EventType,Integer> {
}
