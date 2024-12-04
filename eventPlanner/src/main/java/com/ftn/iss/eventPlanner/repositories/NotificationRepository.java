package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Account;
import com.ftn.iss.eventPlanner.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {
}
