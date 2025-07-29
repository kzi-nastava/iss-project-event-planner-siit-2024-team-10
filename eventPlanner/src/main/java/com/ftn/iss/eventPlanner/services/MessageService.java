package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.message.CreateMessageDTO;
import com.ftn.iss.eventPlanner.dto.message.CreatedMessageDTO;
import com.ftn.iss.eventPlanner.dto.message.GetChatContact;
import com.ftn.iss.eventPlanner.dto.message.GetMessageDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.MessageRepository;
import com.ftn.iss.eventPlanner.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    private ModelMapper modelMapper = new ModelMapper();

    public List<GetMessageDTO> filterMessages(int senderId, int receiverId){
        List<Message> messages = messageRepository.findAll();
        return messages.stream()
                .filter(message ->
                        (message.getSender().getId() == senderId && message.getReceiver().getId() == receiverId) ||
                                (message.getSender().getId() == receiverId && message.getReceiver().getId() == senderId))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .map(message -> modelMapper.map(message, GetMessageDTO.class))
                .collect(Collectors.toList());
    }
    /*
    If we only work with account and not user - display username instead of name and last name
     */
    public List<GetChatContact> getChatContacts(int userId) {
        List<Message> allMessages = messageRepository.findBySenderIdOrReceiverId(userId, userId);

        Map<Integer, Message> latestMessageMap = new HashMap<>();

        for (Message message : allMessages) {
            int contactId = message.getSender().getId() == userId ? message.getReceiver().getId() : message.getSender().getId();

            Message existingMessage = latestMessageMap.get(contactId);
            if (existingMessage == null || message.getTimestamp().isAfter(existingMessage.getTimestamp())) {
                latestMessageMap.put(contactId, message);
            }
        }

        List<GetChatContact> contacts = new ArrayList<>();

        for (Map.Entry<Integer, Message> entry : latestMessageMap.entrySet()) {
            Message latestMessage = entry.getValue();
            int contactId = entry.getKey();

            Account account = accountRepository.findById(contactId).get();
            try {
                User contactUser = userRepository.findById(account.getUser().getId()).orElse(null);
                if (contactUser != null) {
                    GetChatContact contact = new GetChatContact();
                    contact.setUser(contactId);
                    contact.setName(contactUser.getFirstName() + " " + contactUser.getLastName());
                    contact.setContent(latestMessage.getContent());
                    contact.setDateTime(latestMessage.getTimestamp());

                    contacts.add(contact);
                }
            }catch (Exception exception){
                GetChatContact contact = new GetChatContact();
                contact.setUser(contactId);
                contact.setName(account.getUsername());
                contact.setContent(latestMessage.getContent());
                contact.setDateTime(latestMessage.getTimestamp());

                contacts.add(contact);
            }
        }

        // Sort contacts by datetime of last message (most recent first)
        contacts.sort((c1, c2) -> c2.getDateTime().compareTo(c1.getDateTime()));

        return contacts;
    }
    public CreatedMessageDTO create(CreateMessageDTO messageDTO) {
        Account sender = accountRepository.findById(messageDTO.getSender()).orElseThrow(() -> new NotFoundException("Account with ID " + messageDTO.getSender() + " not found"));
        Account receiver = accountRepository.findById(messageDTO.getReceiver()).orElseThrow(() -> new NotFoundException("Account with ID " + messageDTO.getReceiver() + " not found"));
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(LocalDateTime.now());
        message = messageRepository.save(message);
        return mapToCreatedMessageDTO(message);
    }
    public CreatedMessageDTO mapToCreatedMessageDTO(Message message){
        CreatedMessageDTO dto = new CreatedMessageDTO();
        dto.setContent(message.getContent());
        dto.setId(message.getId());
        dto.setReceiver(message.getReceiver().getId());
        dto.setTimestamp(message.getTimestamp());
        dto.setSender(message.getSender().getId());
        return  dto;
    }
}
