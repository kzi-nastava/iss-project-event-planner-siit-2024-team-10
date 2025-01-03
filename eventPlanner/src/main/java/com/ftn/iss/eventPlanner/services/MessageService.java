package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.message.GetMessageDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.model.Message;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.repositories.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
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
}
