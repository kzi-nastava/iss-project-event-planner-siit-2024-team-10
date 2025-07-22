package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.*;
import com.ftn.iss.eventPlanner.dto.offeringcategory.*;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service

public class OfferingCategoryService {
    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;
    @Autowired
    private OfferingRepository offeringRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ModelMapper modelMapper;

    public List<GetOfferingCategoryDTO> findAll(){
        List<OfferingCategory> offeringCategorys = offeringCategoryRepository.findAll();
        return offeringCategorys.stream()
                .map(offeringCategory -> modelMapper.map(offeringCategory, GetOfferingCategoryDTO.class))
                .filter(offeringCategory -> !offeringCategory.isDeleted())
                .collect(Collectors.toList());
    }

    public GetOfferingCategoryDTO findById(int id) {
        OfferingCategory category = offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " not found"));
        return modelMapper.map(category, GetOfferingCategoryDTO.class);
    }
    public CreatedOfferingCategoryDTO create(CreateOfferingCategoryDTO createOfferingCategoryDTO){
        OfferingCategory offeringCategory = new OfferingCategory();
        modelMapper.map(createOfferingCategoryDTO,offeringCategory);
        offeringCategory.setDeleted(false);
        offeringCategory.setPending(true);
        offeringCategory.setCreatorId(createOfferingCategoryDTO.getCreatorId());
        offeringCategory = offeringCategoryRepository.save(offeringCategory);
        return modelMapper.map(offeringCategory,CreatedOfferingCategoryDTO.class);
    }
    public UpdatedOfferingCategoryDTO update(int id, UpdateOfferingCategoryDTO updateOfferingCategoryDTO) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " not found"));

        offeringCategory.setName(updateOfferingCategoryDTO.getName());
        offeringCategory.setDescription(updateOfferingCategoryDTO.getDescription());
        offeringCategory = offeringCategoryRepository.save(offeringCategory);
        approve(id, "Your category has been updated and approved");
        return modelMapper.map(offeringCategory, UpdatedOfferingCategoryDTO.class);
    }

    public boolean delete(int id) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " not found"));
        if (!hasOfferings(id)){
            offeringCategory.setDeleted(true);
            offeringCategoryRepository.save(offeringCategory);
            return true;
        }
        return false;
    }
    public void approve(int id, String title){
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " not found"));
        offeringCategory.setPending(false);
        offeringCategoryRepository.save(offeringCategory);
        for(Offering offering : offeringRepository.findAll()){
            if(offering.getCategory().getId() == id) {
                offering.setPending(false);
                offeringRepository.save(offering);
            }
        }
        // if not admin
        if(offeringCategory.getCreatorId()!=0)
            notificationService.sendNotification(offeringCategory.getCreatorId(),title, "Your category" + offeringCategory.getName() + " " + " with description " + offeringCategory.getDescription() + " - your offerings have been approved and are now visible on your page!");
    }

    public boolean hasOfferings(int id) {
        List<Offering> offerings = offeringRepository.findAll();
        for (Offering offering : offerings)
            if (offering.getCategory().getId() == id && !offering.isDeleted()) {
                return true;
            }
        return false;
    }
}
