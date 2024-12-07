package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
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
}
