package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider,Integer> {
}
