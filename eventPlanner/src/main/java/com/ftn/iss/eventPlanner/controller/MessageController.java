package com.ftn.iss.eventPlanner.controller;
import com.ftn.iss.eventPlanner.model.Message;
import com.ftn.iss.eventPlanner.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{senderId}/{receiverId}")
    public List<Message> getMessages(@PathVariable int senderId, @PathVariable int receiverId) {
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                senderId, receiverId, senderId, receiverId);
    }
}