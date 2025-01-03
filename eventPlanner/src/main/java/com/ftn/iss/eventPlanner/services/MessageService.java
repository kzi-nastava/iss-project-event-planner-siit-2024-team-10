package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import com.ftn.iss.eventPlanner.dto.message.CreateMessageDTO;
import com.ftn.iss.eventPlanner.dto.message.CreatedMessageDTO;
import com.ftn.iss.eventPlanner.dto.message.GetMessageDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.model.AccountReport;
import com.ftn.iss.eventPlanner.model.Location;
import com.ftn.iss.eventPlanner.model.Message;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountRepository accountRepository;
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
    public CreatedMessageDTO create(CreateMessageDTO messageDTO){
        try{
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setSender(accountRepository.findById(messageDTO.getSender())
                .orElseThrow(() -> new AccountNotFoundException("Account not found")));
        message.setReceiver(accountRepository.findById(messageDTO.getReceiver())
                .orElseThrow(() -> new AccountNotFoundException("Account not found")));
        message.setTimestamp(LocalDateTime.now());
        message = messageRepository.save(message);
            return mapToCreatedMessageDTO(message);
        }catch (AccountNotFoundException exception){
            return null;
        }
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
