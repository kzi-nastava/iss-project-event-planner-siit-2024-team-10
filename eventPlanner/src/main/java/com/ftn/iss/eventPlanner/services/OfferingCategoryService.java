package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.*;
import com.ftn.iss.eventPlanner.dto.offeringcategory.*;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
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
    private ModelMapper modelMapper = new ModelMapper();

    public List<GetOfferingCategoryDTO> findAll(){
        List<OfferingCategory> offeringCategorys = offeringCategoryRepository.findAll();
        return offeringCategorys.stream()
                .map(offeringCategory -> modelMapper.map(offeringCategory, GetOfferingCategoryDTO.class))
                .collect(Collectors.toList());
    }

    public GetOfferingCategoryDTO findById(int id) {
        OfferingCategory category = offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + id + " not found"));
        return modelMapper.map(category, GetOfferingCategoryDTO.class);
    }
    public CreatedOfferingCategoryDTO create(CreateOfferingCategoryDTO createOfferingCategoryDTO){
        OfferingCategory offeringCategory = new OfferingCategory();
        modelMapper.map(createOfferingCategoryDTO,offeringCategory);
        offeringCategory.setDeleted(false);
        offeringCategory.setPending(true);
        offeringCategory = offeringCategoryRepository.save(offeringCategory);
        return modelMapper.map(offeringCategory,CreatedOfferingCategoryDTO.class);
    }
    public UpdatedOfferingCategoryDTO update(int id, UpdateOfferingCategoryDTO updateEventTypeDTO) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " not found"));

        modelMapper.map(updateEventTypeDTO, offeringCategory);
        offeringCategory = offeringCategoryRepository.save(offeringCategory);
        return modelMapper.map(offeringCategory, UpdatedOfferingCategoryDTO.class);
    }

    public void delete(int id) {
        OfferingCategory offeringCategory = offeringCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category with ID " + id + " not found"));
        offeringCategory.setDeleted(true);
        offeringCategoryRepository.save(offeringCategory);
    }
}
