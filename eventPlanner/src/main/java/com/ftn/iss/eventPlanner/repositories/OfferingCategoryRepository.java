package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferingCategoryRepository extends JpaRepository<OfferingCategory,Integer> {
}
