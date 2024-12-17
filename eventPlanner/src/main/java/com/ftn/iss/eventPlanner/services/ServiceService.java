package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatePricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.service.*;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.model.specification.ServiceSpecification;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import com.ftn.iss.eventPlanner.repositories.ProviderRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<GetServiceDTO> findAll(
        String name,
        Integer eventTypeId,
        Integer categoryId,
        Double minPrice,
        Double maxPrice,
        Boolean searchByAvailability
    ) {
        Specification<Service> serviceSpecification = Specification.where(ServiceSpecification.hasName(name))
                .and(ServiceSpecification.hasEventTypeId(eventTypeId))
                .and(ServiceSpecification.hasCategoryId(categoryId))
                .and(ServiceSpecification.betweenPrices(minPrice, maxPrice))
                .and(ServiceSpecification.isAvailable(searchByAvailability));

        return serviceRepository.findAll(serviceSpecification).stream()
                .map(service -> modelMapper.map(service, GetServiceDTO.class))
                .collect(Collectors.toList());
    }
    public PagedResponse<GetServiceDTO> findAll(
            Pageable pagable,
            String name,
            Integer eventTypeId,
            Integer categoryId,
            Double minPrice,
            Double maxPrice,
            Boolean searchByAvailability
    ) {
        Specification<Service> serviceSpecification = Specification.where(ServiceSpecification.hasName(name))
                .and(ServiceSpecification.hasEventTypeId(eventTypeId))
                .and(ServiceSpecification.hasCategoryId(categoryId))
                .and(ServiceSpecification.betweenPrices(minPrice, maxPrice))
                .and(ServiceSpecification.isAvailable(searchByAvailability));

        Page<Service> pagedServices = serviceRepository.findAll(serviceSpecification, pagable);

        List<GetServiceDTO> serviceDTOs = pagedServices.getContent().stream()
                .map(this::mapToGetServiceDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(serviceDTOs,pagedServices.getTotalPages(),pagedServices.getTotalElements());
    }
    public GetServiceDTO findById(int id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        return mapToGetServiceDTO(service);
    }
    /*
    add current details to history and set new current
     */
    public UpdatedServiceDTO update(int id, UpdateServiceDTO updateServiceDTO) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));

        // Create a copy of current details before adding to history
        ServiceDetails historicalDetails = new ServiceDetails();
        BeanUtils.copyProperties(service.getCurrentDetails(), historicalDetails);

        service.getDetailsHistory().add(historicalDetails);
        modelMapper.map(updateServiceDTO, service.getCurrentDetails());
        service.getCurrentDetails().setTimestamp(LocalDateTime.now());

        return modelMapper.map(serviceRepository.save(service), UpdatedServiceDTO.class);
    }

    public UpdatedServiceDTO updatePrice(int id, UpdatePricelistItemDTO updateServiceDTO) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));

        ServiceDetails newCurrent = new ServiceDetails();
        newCurrent.setName(service.getCurrentDetails().getName());
        newCurrent.setDescription(service.getCurrentDetails().getDescription());
        newCurrent.setSpecification(service.getCurrentDetails().getSpecification());
        newCurrent.setPrice(updateServiceDTO.getPrice());
        newCurrent.setDiscount(updateServiceDTO.getDiscount());

        newCurrent.setPhotos(
                service.getCurrentDetails().getPhotos() != null
                        ? new ArrayList<>(service.getCurrentDetails().getPhotos())
                        : new ArrayList<>()
        );

        newCurrent.setFixedTime(service.getCurrentDetails().isFixedTime());
        newCurrent.setMaxDuration(service.getCurrentDetails().getMaxDuration());
        newCurrent.setMinDuration(service.getCurrentDetails().getMinDuration());
        newCurrent.setCancellationPeriod(service.getCurrentDetails().getCancellationPeriod());
        newCurrent.setReservationPeriod(service.getCurrentDetails().getReservationPeriod());
        newCurrent.setVisible(service.getCurrentDetails().isVisible());
        newCurrent.setAvailable(service.getCurrentDetails().isAvailable());
        newCurrent.setAutoConfirm(service.getCurrentDetails().isAutoConfirm());
        newCurrent.setTimestamp(LocalDateTime.now());

        ServiceDetails historicalDetails = service.getCurrentDetails();
        service.getDetailsHistory().add(historicalDetails);

        service.setCurrentDetails(newCurrent);

        // Sačuvaj uslugu
        Service savedService = serviceRepository.save(service);

        return modelMapper.map(savedService, UpdatedServiceDTO.class);
    }

    public int findMaxDetailsIdAcrossAllServices() {
        return serviceRepository.findAll().stream()
                .flatMap(service -> {
                    Stream<Integer> historyIds = service.getDetailsHistory().stream()
                            .map(ServiceDetails::getId);
                    Stream<Integer> currentId = service.getCurrentDetails() != null
                            ? Stream.of(service.getCurrentDetails().getId())
                            : Stream.empty();
                    return Stream.concat(historyIds, currentId);
                })
                .mapToInt(id -> id) // Konvertujemo u int stream
                .max()
                .orElse(0); // Vraćamo 0 ako nema elemenata
    }

    public void delete(int id) {
        Service service = (Service) serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        service.setDeleted(true);
        serviceRepository.save(service);
    }
    private GetServiceDTO mapToGetServiceDTO(Service service) {
        GetServiceDTO dto = new GetServiceDTO();

        dto.setId(service.getId());
        dto.setCategory(modelMapper.map(service.getCategory(), GetOfferingCategoryDTO.class));
        dto.setPending(service.isPending());
        dto.setProvider(setGetProviderDTO(service));
        dto.setName(service.getCurrentDetails().getName());
        dto.setDescription(service.getCurrentDetails().getDescription());
        dto.setSpecification(service.getCurrentDetails().getSpecification());
        dto.setPrice(service.getCurrentDetails().getPrice());
        dto.setDiscount(service.getCurrentDetails().getDiscount());
        dto.setPhotos(service.getCurrentDetails().getPhotos());
        dto.setAvailable(service.getCurrentDetails().isAvailable());
        dto.setDeleted(service.isDeleted());
        dto.setVisible(service.getCurrentDetails().isVisible());
        dto.setMaxDuration(service.getCurrentDetails().getMaxDuration());
        dto.setMinDuration(service.getCurrentDetails().getMinDuration());
        dto.setCancellationPeriod(service.getCurrentDetails().getCancellationPeriod());
        dto.setReservationPeriod(service.getCurrentDetails().getReservationPeriod());
        dto.setAutoConfirm(service.getCurrentDetails().isAutoConfirm());
        return dto;
    }
    private GetProviderDTO setGetProviderDTO(Offering offering){
        GetProviderDTO providerDTO = new GetProviderDTO();
        providerDTO.setId(offering.getProvider().getId());
        providerDTO.setEmail(offering.getProvider().getAccount().getEmail());
        providerDTO.setFirstName(offering.getProvider().getFirstName());
        providerDTO.setLastName(offering.getProvider().getLastName());
        providerDTO.setPhoneNumber(offering.getProvider().getPhoneNumber());
        providerDTO.setProfilePhoto(offering.getProvider().getProfilePhoto());
        providerDTO.setLocation(modelMapper.map(offering.getProvider().getLocation(), GetLocationDTO.class));
        providerDTO.setCompany(setGetCompanyDTO(offering));
        return providerDTO;
    }
    private GetCompanyDTO setGetCompanyDTO(Offering offering){
        GetCompanyDTO companyDTO = new GetCompanyDTO();
        companyDTO.setName(offering.getProvider().getCompany().getName());
        companyDTO.setEmail(offering.getProvider().getAccount().getEmail());
        companyDTO.setDescription(offering.getProvider().getCompany().getDescription());
        companyDTO.setPhoneNumber(offering.getProvider().getCompany().getPhoneNumber());
        companyDTO.setPhotos(offering.getProvider().getCompany().getPhotos());
        companyDTO.setLocation(modelMapper.map(offering.getProvider().getCompany().getLocation(), GetLocationDTO.class));
        companyDTO.setPhoneNumber(offering.getProvider().getCompany().getPhoneNumber());

        return companyDTO;
    }
}
