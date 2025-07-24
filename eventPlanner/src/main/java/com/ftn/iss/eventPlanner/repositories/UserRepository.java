package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Location;
import com.ftn.iss.eventPlanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByAccountId(Integer accountId);
}
