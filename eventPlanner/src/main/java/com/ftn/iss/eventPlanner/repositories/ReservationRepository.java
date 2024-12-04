package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation,Integer> {
}
