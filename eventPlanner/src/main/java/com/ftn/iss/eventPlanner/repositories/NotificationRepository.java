package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Account,Integer> {
}
