package com.ftn.iss.eventPlanner.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // REST endpoint for sending messages
    @CrossOrigin(origins = "*") // Allow all origins for development
    @RequestMapping(value = "/send_message_rest", method = RequestMethod.POST)
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> message) {
        if (message.containsKey("message") || message.containsKey("content")) {
            String content = (String) (message.get("message") != null ? message.get("message") : message.get("content"));
            Object fromId = message.get("fromId") != null ? message.get("fromId") : message.get("senderId");
            Object toId = message.get("toId") != null ? message.get("toId") : message.get("receiverId");

            // Add timestamp if not present
            if (!message.containsKey("timestamp")) {
                message.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            // Send to both users involved in the conversation
            if (toId != null && !toId.toString().isEmpty()) {
                // Send to receiver
                this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + toId.toString(), message);
                // Send to sender (for multi-device sync)
                this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + fromId.toString(), message);

                System.out.println("Message sent via REST to users: " + fromId + " and " + toId);
                System.out.println("Message content: " + content);
            } else {
                // Broadcast to all if no specific recipient
                this.simpMessagingTemplate.convertAndSend("/socket-publisher", message);
            }

            return new ResponseEntity<>(message, new HttpHeaders(), HttpStatus.OK);
        }

        return new ResponseEntity<>("Invalid message format", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // WebSocket message handler
    @MessageMapping("/send/message")
    public void broadcastNotification(String message) {
        System.out.println("Received WebSocket message: " + message);

        Map<String, Object> messageConverted = parseMessage(message);

        if (messageConverted != null) {
            // Add timestamp if not present
            if (!messageConverted.containsKey("timestamp")) {
                messageConverted.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            Object fromId = messageConverted.get("fromId");
            Object toId = messageConverted.get("toId");
            String content = (String) messageConverted.get("message");

            if (toId != null && !toId.toString().isEmpty() && fromId != null) {
                // Send to receiver
                this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + toId.toString(), messageConverted);
                // Send to sender (for multi-device sync)
                this.simpMessagingTemplate.convertAndSend("/socket-publisher/" + fromId.toString(), messageConverted);

                System.out.println("Message sent via WebSocket to users: " + fromId + " and " + toId);
                System.out.println("Message content: " + content);
            } else {
                // Broadcast to all if no specific recipient
                this.simpMessagingTemplate.convertAndSend("/socket-publisher", messageConverted);
                System.out.println("Message broadcasted to all users");
            }
        } else {
            System.err.println("Failed to parse WebSocket message: " + message);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> retVal;

        try {
            retVal = mapper.readValue(message, Map.class);
        } catch (IOException e) {
            System.err.println("Error parsing message: " + e.getMessage());
            retVal = null;
        }

        return retVal;
    }
}