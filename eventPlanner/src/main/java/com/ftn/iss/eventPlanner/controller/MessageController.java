package com.ftn.iss.eventPlanner.controller;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER', 'ADMIN', 'AUTHENTICATED_USER')")
    @GetMapping(value = "/{senderId}/{receiverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetMessageDTO>> getBySenderIdAndProviderId(@PathVariable int senderId, @PathVariable int receiverId) {
        List<GetMessageDTO> messages = messageService.filterMessages(senderId,receiverId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER', 'ADMIN', 'AUTHENTICATED_USER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedMessageDTO> createMessage(@Valid @RequestBody CreateMessageDTO createMessageDTO) {
        CreatedMessageDTO createdMessageDTO = messageService.create(createMessageDTO);
        return new ResponseEntity<>(createdMessageDTO, HttpStatus.CREATED);
    }
    @PreAuthorize("hasAnyAuthority('EVENT_ORGANIZER','PROVIDER', 'ADMIN', 'AUTHENTICATED_USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<List<GetChatContact>> getChatContacts(@PathVariable int userId) {
        List<GetChatContact> contacts = messageService.getChatContacts(userId);
        return ResponseEntity.ok(contacts);
    }
}