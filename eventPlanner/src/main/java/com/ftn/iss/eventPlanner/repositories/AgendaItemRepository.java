package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaItemRepository extends JpaRepository<AgendaItem,Integer> {
}
