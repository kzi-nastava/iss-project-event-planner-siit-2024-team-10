package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetItemRepository extends JpaRepository<BudgetItem,Integer> {
}
