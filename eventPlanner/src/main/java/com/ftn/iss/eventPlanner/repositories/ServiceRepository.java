package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRepository extends JpaRepository<Service,Integer>, JpaSpecificationExecutor<Service> {
}
