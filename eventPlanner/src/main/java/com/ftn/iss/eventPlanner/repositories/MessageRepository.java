package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE m.sender.id = :senderId OR m.receiver.id = :receiverId")
    List<Message> findBySenderIdOrReceiverId(int senderId, int receiverId);
}