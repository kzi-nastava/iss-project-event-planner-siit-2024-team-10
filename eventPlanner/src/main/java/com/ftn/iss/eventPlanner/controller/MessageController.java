package com.ftn.iss.eventPlanner.controller;
import com.ftn.iss.eventPlanner.dto.message.GetMessageDTO;
import com.ftn.iss.eventPlanner.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @GetMapping(value = "/{senderId}/{receiverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GetMessageDTO> getBySenderIdAndProviderId(@PathVariable int senderId, @PathVariable int receiverId) {
        List<GetMessageDTO> messages = messageService.filterMessages(senderId,receiverId);
        return new ResponseEntity<>(messages, HttpStatus.OK).getBody();
    }
}