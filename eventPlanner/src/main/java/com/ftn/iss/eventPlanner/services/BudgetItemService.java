package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.budgetitem.*;
import com.ftn.iss.eventPlanner.dto.eventtype.*;
import com.ftn.iss.eventPlanner.model.BudgetItem;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.repositories.BudgetItemRepository;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetItemService {
    @Autowired
    private BudgetItemRepository budgetItemRepository;
    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;
    @Autowired
    private EventRepository eventRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public CreatedBudgetItemDTO create(CreateBudgetItemDTO budgetItemDTO){
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setAmount(budgetItem.getAmount());
        budgetItem.setDeleted(false);
        budgetItem.setCategory(offeringCategoryRepository.findById(budgetItemDTO.getCategoryId()).get());
        budgetItem.setEvent(eventRepository.findById(budgetItemDTO.getEventId()).get());
        budgetItem = budgetItemRepository.save(budgetItem);
        Event event = eventRepository.findById(budgetItemDTO.getEventId()).get();
        event.getBudget().add(budgetItem);
        eventRepository.save(event);
        return modelMapper.map(budgetItem,CreatedBudgetItemDTO.class);
    }

    public List<GetBudgetItemDTO> findAll() {
        List<BudgetItem> budgetItems = budgetItemRepository.findAll();
        return budgetItems.stream()
                .map(budgetItem -> modelMapper.map(budgetItem, GetBudgetItemDTO.class))
                .collect(Collectors.toList());
    }

    public GetBudgetItemDTO findById(int id) {
        BudgetItem budgetItems = budgetItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget item with ID " + id + " not found"));
        return modelMapper.map(budgetItems, GetBudgetItemDTO.class);
    }
}

/*
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
}*/
