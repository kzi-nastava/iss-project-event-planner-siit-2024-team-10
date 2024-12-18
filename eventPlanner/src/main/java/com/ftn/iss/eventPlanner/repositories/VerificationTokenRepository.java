package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Integer> {
    public VerificationToken findByToken(String token);
    public VerificationToken findByAccountId(int accountId);
}
