package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferingService {
    @Autowired
    private OfferingRepository offeringRepository;

    private ModelMapper modelMapper = new ModelMapper();

}
