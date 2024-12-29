package com.ftn.iss.eventPlanner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.iss.eventPlanner.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.ftn.iss.eventPlanner.repositories.MessageRepository;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // A thread-safe list to keep track of active WebSocket sessions
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    // Repository for saving messages (assumed to interact with a database)
    @Autowired
    private MessageRepository messageRepository;

    // For serializing and deserializing JSON messages
    @Autowired
    private ObjectMapper objectMapper;

    // Invoked when a new WebSocket connection is established
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session); // Add the new session to the list
    }

    // Invoked when a new message is received from a client
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Deserialize the incoming message to a `Message` object
        Message chatMessage = objectMapper.readValue(message.getPayload(), Message.class);

        // Save the message in the repository (assumes persistence)
        messageRepository.save(chatMessage);

        // Broadcast the message to all connected WebSocket sessions
        for (WebSocketSession webSocketSession : sessions) {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }

    // Invoked when a WebSocket connection is closed
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session); // Remove the closed session from the list
    }
}
