package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Location;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfferingRepository extends JpaRepository<Offering,Integer> {
    @Query("SELECT s FROM Service s")
    List<Service> findAllServices();
}
