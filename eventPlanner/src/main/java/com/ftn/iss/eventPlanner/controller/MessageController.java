package com.ftn.iss.eventPlanner.controller;
import com.ftn.iss.eventPlanner.dto.message.GetMessageDTO;
import com.ftn.iss.eventPlanner.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/{senderId}/{receiverId}")
    public List<GetMessageDTO> getBySenderIdAndProviderId(@PathVariable int senderId, @PathVariable int receiverId) {
        List<GetMessageDTO> messages = messageService.findBySenderIdAndReceiverId(senderId,receiverId);
        return new ResponseEntity<>(messages, HttpStatus.OK).getBody();
    }
}