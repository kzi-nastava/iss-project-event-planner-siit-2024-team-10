package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Integer> {
}
