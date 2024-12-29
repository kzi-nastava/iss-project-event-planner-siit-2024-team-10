package com.ftn.iss.eventPlanner.repositories;

import com.ftn.iss.eventPlanner.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer> {
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            int senderId1, int receiverId1, int senderId2, int receiverId2);
}