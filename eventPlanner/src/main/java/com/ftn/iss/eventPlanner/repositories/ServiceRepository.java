package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ServiceRepository extends JpaRepository<Service,Integer>, JpaSpecificationExecutor<Service> {
    @Query("SELECT MAX(s.currentDetails.price) FROM Service s")
    Double findMaxServicePrice();
}
