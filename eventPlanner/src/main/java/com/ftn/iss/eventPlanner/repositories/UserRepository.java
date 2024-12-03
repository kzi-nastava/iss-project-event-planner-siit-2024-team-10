package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
