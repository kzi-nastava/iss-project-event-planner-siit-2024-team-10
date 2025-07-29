package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatePricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatedPricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.service.*;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.exception.ServiceHasReservationsException;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.model.specification.ServiceSpecification;
import com.ftn.iss.eventPlanner.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.webjars.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private FileService fileService;
    @Autowired
    private ModelMapper modelMapper;

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
            Account creator = accountRepository.findByUserId(serviceDTO.getProvider())
                    .orElseThrow(() -> new NotFoundException("Account with user ID " + serviceDTO.getProvider() + " not found"));
            category.setCreatorId(creator.getId());
            category=offeringCategoryRepository.save(category);
            service.setCategory(category);
            service.setPending(true);
        }
        else{
            OfferingCategory category = offeringCategoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category with ID " + serviceDTO.getCategoryId() + " not found"));
            service.setCategory(category);
        }
        Provider provider = providerRepository.findById(serviceDTO.getProvider())
                .orElseThrow(() -> new NotFoundException("Provider with ID " + serviceDTO.getProvider() + " not found"));
        service.setProvider(provider);

        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setName(serviceDTO.getName());
        serviceDetails.setDescription(serviceDTO.getDescription());
        serviceDetails.setSpecification(serviceDTO.getSpecification());
        serviceDetails.setPrice(serviceDTO.getPrice());
        serviceDetails.setDiscount(serviceDTO.getDiscount());
        if(serviceDTO.getPhotos()!=null){
            for(String photo : serviceDTO.getPhotos()) {
                if(!fileService.filesExist(photo)){
                    throw new IllegalArgumentException("Invalid file name.");
                }
            }
        }
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
                .orElseThrow(() -> new NotFoundException("Service with ID " + id + " not found"));
        return mapToGetServiceDTO(service);
    }

    public UpdatedServiceDTO update(int id, UpdateServiceDTO updateServiceDTO) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service with ID " + id + " not found"));

        // Create a copy of current details before adding to history
        ServiceDetails historicalDetails = new ServiceDetails();
        BeanUtils.copyProperties(service.getCurrentDetails(), historicalDetails);

        service.getServiceDetailsHistory().add(historicalDetails);
        ServiceDetails newDetails = new ServiceDetails();
        modelMapper.map(updateServiceDTO, newDetails);
        if(updateServiceDTO.getPhotos()!=null){
            for(String photo : updateServiceDTO.getPhotos()) {
                if(!fileService.filesExist(photo)){
                    throw new IllegalArgumentException("Invalid file name.");
                }
            }
        }
        service.setCurrentDetails(newDetails);
        service.getCurrentDetails().setTimestamp(LocalDateTime.now());

        return modelMapper.map(serviceRepository.save(service).getCurrentDetails(), UpdatedServiceDTO.class);
    }

    public UpdatedPricelistItemDTO updatePrice(int id, UpdatePricelistItemDTO updatePricelistItemDTO) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service with ID " + id + " not found"));

        UpdateServiceDTO serviceDTO = new UpdateServiceDTO();
        serviceDTO.setPrice(updatePricelistItemDTO.getPrice());
        serviceDTO.setDescription(service.getCurrentDetails().getDescription());
        serviceDTO.setDiscount(updatePricelistItemDTO.getDiscount());
        serviceDTO.setAvailable(service.getCurrentDetails().isAvailable());
        serviceDTO.setName(service.getCurrentDetails().getName());
        serviceDTO.setId(service.getId());
        serviceDTO.setPhotos(service.getCurrentDetails().getPhotos());
        serviceDTO.setVisible(service.getCurrentDetails().isVisible());
        serviceDTO.setCancellationPeriod(service.getCurrentDetails().getCancellationPeriod());
        serviceDTO.setAutoConfirm(service.getCurrentDetails().isAutoConfirm());
        serviceDTO.setMinDuration(service.getCurrentDetails().getMinDuration());
        serviceDTO.setMaxDuration(service.getCurrentDetails().getMaxDuration());
        serviceDTO.setSpecification(service.getCurrentDetails().getSpecification());
        serviceDTO.setReservationPeriod(service.getCurrentDetails().getReservationPeriod());

        update(service.getId(),serviceDTO);

        UpdatedPricelistItemDTO dto = new UpdatedPricelistItemDTO();
        dto.setId(service.getId());
        dto.setPrice(updatePricelistItemDTO.getPrice());
        dto.setOfferingId(service.getId());
        dto.setDiscount(updatePricelistItemDTO.getDiscount());
        dto.setName(service.getCurrentDetails().getName());
        return dto;
    }

    public void delete(int id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service with ID " + id + " not found"));

        int currentDetailsId = service.getCurrentDetails().getId();
        Set<Integer> historyIds = service.getServiceDetailsHistory()
                .stream()
                .map(ServiceDetails::getId)
                .collect(Collectors.toSet());

        boolean hasReservation = budgetItemRepository.findAll().stream().anyMatch(budgetItem ->
                budgetItem.getServices().stream().anyMatch(details ->
                        details.getId() == currentDetailsId || historyIds.contains(details.getId())
                )
        );

        if (hasReservation) {
            throw new ServiceHasReservationsException("Service cannot be deleted because it has active or past reservations.");
        }

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