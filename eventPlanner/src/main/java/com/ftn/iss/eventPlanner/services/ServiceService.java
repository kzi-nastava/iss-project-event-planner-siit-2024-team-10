package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.service.*;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private OfferingRepository offeringRepository;

    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Creates a new service based on the provided DTO.
     * - If the category exists, the service is created and associated with it.
     * - If the category is proposed, the service is set to a pending state.
     *
     * @param serviceDTO Data for creating the service.
     * @return A DTO representing the created service.
     */
    public CreatedServiceDTO create(CreateServiceDTO serviceDTO) {
        Service service = new Service();
        modelMapper.map(serviceDTO, service);

        // Check if the category is pending
        // TODO: category will be created and added with id but not yet approved
        OfferingCategory category = offeringCategoryRepository.findById(serviceDTO.getCategoryId()).get();

        if (category.isPending()) {
            service.setPending(true);
        } else {
            service.setCategory(category);
            service.setPending(false);
        }

        Service savedService = offeringRepository.save(service);

        return modelMapper.map(savedService, CreatedServiceDTO.class);
    }
    public List<GetServiceDTO> findAll() {
        List<Service> services = offeringRepository.findAllServices();
        return services.stream()
                .map(service -> modelMapper.map(service, GetServiceDTO.class))
                .collect(Collectors.toList());
    }
    public GetServiceDTO findById(int id) {
        Service service = (Service) offeringRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        return modelMapper.map(service, GetServiceDTO.class);
    }
    /*
    add current details to history and set new current
     */
    public UpdatedServiceDTO update(int id, UpdateServiceDTO updateServiceDTO) {
        Service service = (Service) offeringRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        service.getDetailsHistory().add(service.getCurrentDetails());
        modelMapper.map(updateServiceDTO, service.getCurrentDetails());
        service = offeringRepository.save(service);
        return modelMapper.map(service, UpdatedServiceDTO.class);
    }
    public void delete(int id) {
        Service service = (Service) offeringRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        service.setDeleted(true);
        offeringRepository.save(service);
    }
}
