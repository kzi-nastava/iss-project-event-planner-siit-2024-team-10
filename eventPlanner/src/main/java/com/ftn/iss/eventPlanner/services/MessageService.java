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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    private ModelMapper modelMapper = new ModelMapper();

    public List<GetMessageDTO> findBySenderIdAndReceiverId(int senderId, int
                                        receiverId){
        List<Message> messages = messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                senderId, receiverId, senderId, receiverId);
        return messages.stream()
                .map(message -> modelMapper.map(message, GetMessageDTO.class))
                .collect(Collectors.toList());
    }
}
