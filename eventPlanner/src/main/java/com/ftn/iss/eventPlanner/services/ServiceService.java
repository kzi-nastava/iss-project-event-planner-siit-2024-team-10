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
import com.ftn.iss.eventPlanner.repositories.*;
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
    private AccountRepository accountRepository;

    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;
    @Autowired
    private BudgetItemRepository budgetItemRepository;
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
        Service service = new Service();
        service.setPending(false);
        if(serviceDTO.getCategoryId() == 0){
            if(serviceDTO.getCategoryProposalName()==null)
                throw new IllegalArgumentException("Category proposal is required");
            OfferingCategory category = new OfferingCategory();
            category.setName(serviceDTO.getCategoryProposalName());
            category.setDescription(serviceDTO.getCategoryProposalDescription());
            category.setPending(true);
            category.setCreatorId(accountRepository.findByUserId(serviceDTO.getProvider()).get().getId());
            category=offeringCategoryRepository.save(category);
            service.setCategory(category);
            service.setPending(true);
        }
        else{
            OfferingCategory category = offeringCategoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category with ID " + serviceDTO.getCategoryId() + " not found"));
            service.setCategory(category);
        }
        Provider provider = providerRepository.findById(serviceDTO.getProvider()).get();
        service.setProvider(provider);

        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setName(serviceDTO.getName());
        serviceDetails.setDescription(serviceDTO.getDescription());
        serviceDetails.setSpecification(serviceDTO.getSpecification());
        serviceDetails.setPrice(serviceDTO.getPrice());
        serviceDetails.setDiscount(serviceDTO.getDiscount());
        serviceDetails.setPhotos(serviceDTO.getPhotos());
        serviceDetails.setVisible(serviceDTO.isVisible());
        serviceDetails.setAvailable(serviceDTO.isAvailable());
        serviceDetails.setTimestamp(LocalDateTime.now());
        serviceDetails.setMaxDuration(serviceDTO.getMaxDuration());
        serviceDetails.setMinDuration(serviceDTO.getMinDuration());
        serviceDetails.setReservationPeriod(serviceDTO.getReservationPeriod());
        serviceDetails.setCancellationPeriod(serviceDTO.getCancellationPeriod());
        serviceDetails.setAutoConfirm(serviceDTO.isAutoConfirm());

        service.setCurrentDetails(serviceDetails);
        service = serviceRepository.save(service);
        return modelMapper.map(service, CreatedServiceDTO.class);
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

        service.getServiceDetailsHistory().add(historicalDetails);
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
        service.getServiceDetailsHistory().add(historicalDetails);

        service.setCurrentDetails(newCurrent);

        Service savedService = serviceRepository.save(service);

        return modelMapper.map(savedService, UpdatedServiceDTO.class);
    }

    public boolean delete(int id) {
        Service service = (Service) serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));

        for (BudgetItem budgetItem : budgetItemRepository.findAll()) {
            for (ServiceDetails serviceDetails : budgetItem.getServices()) {
                if (
                        service.getCurrentDetails().getId() == serviceDetails.getId() ||
                                service.getServiceDetailsHistory().stream().anyMatch(sd -> sd.getId() == serviceDetails.getId())
                ) {
                    return false;
                }
            }
        }

        service.setDeleted(true);
        serviceRepository.save(service);
        return true;
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
        providerDTO.setAccountId(offering.getProvider().getAccount().getId());
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