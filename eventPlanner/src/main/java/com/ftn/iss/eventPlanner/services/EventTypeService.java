package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.CreateEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.CreatedEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.UpdateEventTypeDTO;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.repositories.EventTypeRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventTypeService {
    @Autowired
    private EventTypeRepository eventTypeRepository;
    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;
    private ModelMapper modelMapper = new ModelMapper();

    public CreatedEventTypeDTO create(CreateEventTypeDTO eventTypeDTO){
        EventType eventType = new EventType();
        modelMapper.map(eventTypeDTO,eventType);
        for(int offeringCategoryId: eventTypeDTO.getRecommendedCategoryIds()){
            OfferingCategory category=offeringCategoryRepository.findById(offeringCategoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Offering category with ID " + offeringCategoryId + " not found"));
            eventType.getRecommendedCategories().add(category);
        }
        eventType.setActive(true);
        eventType = eventTypeRepository.save(eventType);
        return modelMapper.map(eventType,CreatedEventTypeDTO.class);
    }
}
