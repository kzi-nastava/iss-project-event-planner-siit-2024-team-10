package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Integer>, JpaSpecificationExecutor<Event> {
    List<Event> findByOrganizerId(Integer organizerId);
}
