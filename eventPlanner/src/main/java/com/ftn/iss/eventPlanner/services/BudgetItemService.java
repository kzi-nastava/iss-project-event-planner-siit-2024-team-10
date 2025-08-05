package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.budgetitem.*;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.product.GetProductDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

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

    public CreatedBudgetItemDTO create(int eventId, CreateBudgetItemDTO budgetItemDTO, int offeringId) {
        // Find event and check if it exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));

        // Check if event is deleted
        if (event.isDeleted()) {
            throw new IllegalArgumentException("Event with ID " + eventId + " is deleted and cannot be modified");
        }

        // Find category and check if it exists
        OfferingCategory category = offeringCategoryRepository.findById(budgetItemDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Offering category with ID " + budgetItemDTO.getCategoryId() + " not found"));

        // Check if category is deleted
        if (category.isDeleted()) {
            throw new IllegalArgumentException("Offering category with ID " + budgetItemDTO.getCategoryId() + " is deleted and cannot be used");
        }
        if (category.isPending()) {
            throw new IllegalArgumentException("Offering category with ID " + budgetItemDTO.getCategoryId() + " is pending and cannot be used");
        }
        // Check if amount is negative
        if (budgetItemDTO.getAmount()<0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        // Create budget item
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setAmount(budgetItemDTO.getAmount());
        budgetItem.setDeleted(false);
        budgetItem.setEvent(event);
        budgetItem.setCategory(category);

        // Handle offering if provided
        if (offeringId != 0) {
            // Find offering and check if it exists
            Offering offering = offeringRepository.findById(offeringId)
                    .orElseThrow(() -> new NotFoundException("Offering with ID " + offeringId + " not found"));

            // Check if offering is deleted
            if (offering.isDeleted()) {
                throw new IllegalArgumentException("Offering with ID " + offeringId + " is deleted and cannot be used");
            }

            String offeringType = offering.getClass().getSimpleName();

            if ("Service".equals(offeringType)) {
                com.ftn.iss.eventPlanner.model.Service service = (com.ftn.iss.eventPlanner.model.Service) offering;
                budgetItem.getServices().add(service.getCurrentDetails());
            } else if ("Product".equals(offeringType)) {
                budgetItem.getProducts().add(((com.ftn.iss.eventPlanner.model.Product)offering).getCurrentDetails());
            }
        }

        // Save budget item
        budgetItem = budgetItemRepository.save(budgetItem);

        // Add to event's budget and save event
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
        BudgetItem budgetItem = budgetItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Budget item with ID " + id + " not found"));
        return mapBudgetItemToDTO(budgetItem);
    }

    public UpdatedBudgetItemDTO updateAmount(int budgetItemId, UpdateBudgetItemDTO dto) {
        BudgetItem budgetItem = budgetItemRepository.findById(budgetItemId)
                .orElseThrow(() -> new NotFoundException("Budget item with ID " + budgetItemId + " not found"));
        if (budgetItem.isDeleted()) {
            throw new IllegalArgumentException("Cannot update deleted budget item");
        }
        double usedAmount = 0;

        for (ServiceDetails service : budgetItem.getServices()) {
            usedAmount += service.getPrice() * (1 - service.getDiscount() / 100.0);
        }

        for (ProductDetails product : budgetItem.getProducts()) {
            usedAmount += product.getPrice() * (1 - product.getDiscount() / 100.0);
        }

        if (dto.getAmount() < usedAmount) {
            throw new IllegalArgumentException("New amount cannot be less than the amount already used (" + usedAmount + ").");
        }

        budgetItem.setAmount(dto.getAmount());
        budgetItem = budgetItemRepository.save(budgetItem);

        return modelMapper.map(budgetItem, UpdatedBudgetItemDTO.class);
    }
    public void delete(int eventId, int budgetItemId) {
        BudgetItem budgetItem = budgetItemRepository.findById(budgetItemId)
                .orElseThrow(() -> new NotFoundException("Budget item with ID " + budgetItemId + " not found"));
        if(budgetItem.isDeleted())
            throw new IllegalArgumentException("Budget item with ID " + budgetItemId + " is deleted");
        if(budgetItem.getServices().size() + budgetItem.getProducts().size() != 0)
            throw new IllegalArgumentException("Budget item cannot be deleted because it has offerings");
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        event.getBudget().remove(budgetItem);
        budgetItem.setEvent(null);
        eventRepository.save(event);
        budgetItem.setDeleted(true);
        budgetItemRepository.save(budgetItem);
    }
    public boolean hasMoneyLeft(BudgetItem budgetItem, double price, double discount) {
        double remainingAmount = budgetItem.getAmount();

        for (ServiceDetails serviceDetails : budgetItem.getServices()) {
            remainingAmount -= serviceDetails.getPrice() * (1 - serviceDetails.getDiscount() / 100.0);
        }

        for (ProductDetails productDetails : budgetItem.getProducts()) {
            remainingAmount -= productDetails.getPrice() * (1 - productDetails.getDiscount() / 100.0);
        }

        remainingAmount -= price * (1 - discount / 100.0);
        return remainingAmount >= 0;
    }
    public void buy(int eventId, int offeringId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new NotFoundException("Offering with ID " + offeringId + " not found"));

        boolean addedToExistingBudgetItem = false;

        for (BudgetItem budgetItem : event.getBudget()) {
            if (budgetItem.getCategory().getId() == offering.getCategory().getId()) {
                String offeringType = offering.getClass().getSimpleName();

                if ("Service".equals(offeringType)) {
                    com.ftn.iss.eventPlanner.model.Service service = (com.ftn.iss.eventPlanner.model.Service) offering;
                    if (!hasMoneyLeft(budgetItem, service.getCurrentDetails().getPrice(), service.getCurrentDetails().getDiscount())) {
                        throw new IllegalArgumentException("Insufficient budget for this purchase");
                    }
                    budgetItem.getServices().add(service.getCurrentDetails());
                    addedToExistingBudgetItem = true;

                } else if ("Product".equals(offeringType)) {
                    Product product = (Product) offering;
                    int currentProductDetailsId = product.getCurrentDetails().getId();

                    boolean alreadyAdded = budgetItem.getProducts().stream()
                            .anyMatch(p -> p.getId() == currentProductDetailsId ||
                                    product.getProductDetailsHistory().stream().anyMatch(h -> h.getId() == p.getId()));

                    if (alreadyAdded) {
                        throw new IllegalArgumentException("Product already purchased");
                    }

                    if (!hasMoneyLeft(budgetItem, product.getCurrentDetails().getPrice(), product.getCurrentDetails().getDiscount())) {
                        throw new IllegalArgumentException("Insufficient budget for this purchase");
                    }

                    budgetItem.getProducts().add(product.getCurrentDetails());
                    addedToExistingBudgetItem = true;
                }

                budgetItemRepository.save(budgetItem);
                break;
            }
        }

        if (!addedToExistingBudgetItem) {
            CreateBudgetItemDTO createBudgetItemDTO = new CreateBudgetItemDTO();
            createBudgetItemDTO.setAmount(0);
            createBudgetItemDTO.setCategoryId(offering.getCategory().getId());
            create(eventId, createBudgetItemDTO, offeringId);
        }
    }


    public List<GetBudgetItemDTO> findByEventId(int eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));
        return budgetItemRepository.findByEventId(eventId)
                .stream()
                .filter(item -> !item.isDeleted())
                .map(this::mapBudgetItemToDTO)
                .collect(Collectors.toList());
    }
    public double getTotalBudgetForEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with ID " + eventId + " not found"));

        return event.getBudget()
                .stream()
                .filter(item -> !item.isDeleted())
                .mapToDouble(BudgetItem::getAmount)
                .sum();
    }
    public GetBudgetItemDTO mapBudgetItemToDTO(BudgetItem budgetItem) {
        GetBudgetItemDTO dto = new GetBudgetItemDTO();
        dto.setId(budgetItem.getId());
        dto.setAmount(budgetItem.getAmount());
        dto.setCategory(modelMapper.map(budgetItem.getCategory(), GetOfferingCategoryDTO.class));
        dto.setDeleted(budgetItem.isDeleted());

        GetOfferingCategoryDTO categoryDTO = modelMapper.map(budgetItem.getCategory(), GetOfferingCategoryDTO.class);

        List<GetServiceDTO> services = budgetItem.getServices()
                .stream()
                .map(sd -> {
                    GetServiceDTO serviceDTO = modelMapper.map(sd, GetServiceDTO.class);
                    serviceDTO.setCategory(categoryDTO);
                    return serviceDTO;
                })
                .collect(Collectors.toList());
        dto.setServices(services);

        List<GetProductDTO> products = budgetItem.getProducts()
                .stream()
                .map(pd -> {
                    GetProductDTO productDTO = modelMapper.map(pd, GetProductDTO.class);
                    productDTO.setCategory(categoryDTO);
                    return productDTO;
                })
                .collect(Collectors.toList());
        dto.setProducts(products);

        return dto;
    }
}