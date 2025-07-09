package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByEmail(String email);
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.favouriteEvents WHERE a.id = :id")
    Optional<Account> findByIdWithFavouriteEvents(@Param("id") int id);

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.favouriteOfferings WHERE a.id = :id")
    Optional<Account> findByIdWithFavouriteOfferings(@Param("id") int id);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    Optional<Account> findByUserId(@Param("userId") int userId);

    @Query("SELECT a FROM Account a JOIN a.acceptedEvents e WHERE e.id = :eventId")
    List<Account> findAccountsByAcceptedEventId(@Param("eventId") int eventId);
}