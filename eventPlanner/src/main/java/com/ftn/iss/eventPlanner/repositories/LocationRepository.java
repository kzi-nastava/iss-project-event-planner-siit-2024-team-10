package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,Integer> {
    @Query("SELECT l FROM Location l WHERE l.country = :country AND l.city = :city AND l.street = :street AND l.houseNumber = :houseNumber")
    Optional<Location> findByAllFields(@Param("country") String country, @Param("city") String city, @Param("street") String street, @Param("houseNumber") String houseNumber);
}
