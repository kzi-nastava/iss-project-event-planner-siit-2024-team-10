package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.*;
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

    public List<GetEventTypeDTO> findAll() {
        List<EventType> eventTypes = eventTypeRepository.findAll();
        return eventTypes.stream()
                .map(eventType -> modelMapper.map(eventType, GetEventTypeDTO.class))
                .collect(Collectors.toList());
    }

    public GetEventTypeDTO findById(int id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + id + " not found"));
        return modelMapper.map(eventType, GetEventTypeDTO.class);
    }

    public UpdatedEventTypeDTO update(int id, UpdateEventTypeDTO updateEventTypeDTO) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + id + " not found"));

        modelMapper.map(updateEventTypeDTO, eventType);

        if (updateEventTypeDTO.getRecommendedCategoryIds() != null) {
            eventType.getRecommendedCategories().clear();
            for (int categoryId : updateEventTypeDTO.getRecommendedCategoryIds()) {
                OfferingCategory category = offeringCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Offering Category ID: " + categoryId));
                eventType.getRecommendedCategories().add(category);
            }
        }

        eventType = eventTypeRepository.save(eventType);
        return modelMapper.map(eventType, UpdatedEventTypeDTO.class);
    }

    public void delete(int id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + id + " not found"));
        eventType.setActive(false);
        eventTypeRepository.save(eventType);
    }

    public void activate(int id){
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + id + " not found"));
        eventType.setActive(true);
        eventTypeRepository.save(eventType);
    }

}
