package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location,Integer> {
}
