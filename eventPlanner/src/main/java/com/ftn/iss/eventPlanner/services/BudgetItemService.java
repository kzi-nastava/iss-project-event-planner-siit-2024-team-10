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
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
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
    @Autowired
    private OfferingRepository offeringRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public CreatedBudgetItemDTO create(int eventId, CreateBudgetItemDTO budgetItemDTO) {
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setAmount(budgetItemDTO.getAmount());
        budgetItem.setDeleted(false);
        budgetItem.setCategory(offeringCategoryRepository.findById(budgetItemDTO.getCategoryId()).get());
        budgetItem.setEvent(eventRepository.findById(budgetItemDTO.getEventId()).get());
        budgetItem = budgetItemRepository.save(budgetItem);
        Event event = eventRepository.findById(eventId).get();
        event.getBudget().add(budgetItem);
        eventRepository.save(event);
        return modelMapper.map(budgetItem, CreatedBudgetItemDTO.class);
    }

    public List<GetBudgetItemDTO> findAll() {
        List<BudgetItem> budgetItems = budgetItemRepository.findAll()
                .stream()
                .collect(Collectors.toList());

        return budgetItems.stream()
                .map(budgetItem -> modelMapper.map(budgetItem, GetBudgetItemDTO.class))
                .collect(Collectors.toList());
    }

    public GetBudgetItemDTO findById(int id) {
        BudgetItem budgetItems = budgetItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget item with ID " + id + " not found"));
        return modelMapper.map(budgetItems, GetBudgetItemDTO.class);
    }

    public UpdatedBudgetItemDTO update(int budgetItemId, UpdateBudgetItemDTO updateBudgetItemDTO) {
        BudgetItem budgetItem = budgetItemRepository.findById(budgetItemId)
                .orElseThrow(() -> new IllegalArgumentException("Budget item with ID " + budgetItemId + " not found"));

        if(updateBudgetItemDTO.getAmount() != 0)
            budgetItem.setAmount(updateBudgetItemDTO.getAmount());

        if(updateBudgetItemDTO.getOfferingId() != 0)
            budgetItem.getOfferings().add(offeringRepository.findById(updateBudgetItemDTO.getOfferingId()).get());

        budgetItem = budgetItemRepository.save(budgetItem);
        return modelMapper.map(budgetItem, UpdatedBudgetItemDTO.class);
    }

    public boolean delete(int eventId, int budgetItemd) {
        BudgetItem budgetItem = budgetItemRepository.findById(budgetItemd)
                .orElseThrow(() -> new IllegalArgumentException("Budget item with ID " + budgetItemd + " not found"));
        if(budgetItem.getOfferings().size() != 0)
            return false;
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event with ID " + eventId + " not found"));
        event.getBudget().remove(budgetItem);
        eventRepository.save(event);
        budgetItem.setDeleted(true);
        budgetItemRepository.save(budgetItem);
        return true;
    }
    public List<GetBudgetItemDTO> findByEventId(int eventId) {
        return budgetItemRepository.findByEventId(eventId)
                .stream()
                .filter(item -> !item.isDeleted())
                .map(budgetItem -> modelMapper.map(budgetItem, GetBudgetItemDTO.class))
                .collect(Collectors.toList());
    }
}
