package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetItemRepository extends JpaRepository<BudgetItem,Integer> {
    List<BudgetItem> findByEventId(int eventId);
}
