package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.service.*;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import com.ftn.iss.eventPlanner.repositories.ProviderRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;
    @Autowired
    private ProviderRepository providerRepository;
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
        Service service = modelMapper.map(serviceDTO,Service.class);
        ServiceDetails currentDetails = modelMapper.map(serviceDTO,ServiceDetails.class);
        service.setId(0);

        // set current details and history
        currentDetails.setTimestamp(LocalDateTime.now());

        // modelMapper.map(serviceDTO, service);
        service.setCurrentDetails(currentDetails);

        // Check if the category is pending
        // TODO: category will be created and added with id but not yet approved
        OfferingCategory category = offeringCategoryRepository.findById(serviceDTO.getCategory()).get();
        service.setCategory(category);

        Provider provider = providerRepository.findById(serviceDTO.getProvider()).get();
        service.setProvider(provider);

        if (category.isPending()) {
            service.setPending(true);
        } else {
            service.setCategory(category);
            service.setPending(false);
        }

        Service savedService = serviceRepository.save(service);

        return modelMapper.map(savedService, CreatedServiceDTO.class);
    }
    public List<GetServiceDTO> findAll() {
        List<Service> services = serviceRepository.findAll();
        return services.stream()
                .map(service -> modelMapper.map(service, GetServiceDTO.class))
                .collect(Collectors.toList());
    }
    public GetServiceDTO findById(int id) {
        Service service = (Service) serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        return modelMapper.map(service, GetServiceDTO.class);
    }
    /*
    add current details to history and set new current
     */
    public UpdatedServiceDTO update(int id, UpdateServiceDTO updateServiceDTO) {
        Service service = (Service) serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        service.getDetailsHistory().add(service.getCurrentDetails());
        modelMapper.map(updateServiceDTO, service.getCurrentDetails());
        service.getCurrentDetails().setTimestamp(LocalDateTime.now());
        service = serviceRepository.save(service);
        return modelMapper.map(service, UpdatedServiceDTO.class);
    }
    public void delete(int id) {
        Service service = (Service) serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        service.setDeleted(true);
        serviceRepository.save(service);
    }
}
