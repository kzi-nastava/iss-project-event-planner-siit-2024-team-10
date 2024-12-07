package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.service.CreateServiceDTO;
import com.ftn.iss.eventPlanner.dto.service.CreatedServiceDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.model.Service;
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
}
