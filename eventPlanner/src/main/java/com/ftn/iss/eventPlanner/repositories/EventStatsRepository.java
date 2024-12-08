package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.EventStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStatsRepository extends JpaRepository<EventStats, Integer> {
}
