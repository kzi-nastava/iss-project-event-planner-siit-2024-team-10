package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfferingRepository extends JpaRepository<Offering,Integer>, JpaSpecificationExecutor<Offering> {
}
