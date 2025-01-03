package com.ftn.iss.eventPlanner.controller;
import com.ftn.iss.eventPlanner.dto.eventtype.CreateEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.CreatedEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.message.CreateMessageDTO;
import com.ftn.iss.eventPlanner.dto.message.CreatedMessageDTO;
import com.ftn.iss.eventPlanner.dto.message.GetChatContact;
import com.ftn.iss.eventPlanner.dto.message.GetMessageDTO;
import com.ftn.iss.eventPlanner.services.MessageService;
import jakarta.validation.Valid;
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
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedMessageDTO> createEventType(@Valid @RequestBody CreateMessageDTO createMessageDTO) {
        try{
            CreatedMessageDTO createdMessageDTO = messageService.create(createMessageDTO);
            return new ResponseEntity<>(createdMessageDTO, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    //     @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/{userId}")
    public ResponseEntity<List<GetChatContact>> getChatContacts(@PathVariable int userId) {
        List<GetChatContact> contacts = messageService.getChatContacts(userId);
        return ResponseEntity.ok(contacts);
    }
}