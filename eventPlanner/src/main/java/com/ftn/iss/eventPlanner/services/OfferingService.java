package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventCardDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingCardDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.model.specification.ProductSpecification;
import com.ftn.iss.eventPlanner.model.specification.ServiceSpecification;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import com.ftn.iss.eventPlanner.repositories.ProductRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class OfferingService {
    @Autowired
    private OfferingRepository offeringRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ProductRepository productRepository;

    private ModelMapper modelMapper = new ModelMapper();


    public List<GetOfferingDTO> findAll(){
        List<Offering> offerings = offeringRepository.findAll();

        return offerings.stream()
                .map(this::mapToGetOfferingDTO)
                .collect(Collectors.toList());
    }

    public List<GetOfferingDTO> getAllOfferings(
            Boolean isServiceFilter,
            String name,
            Integer eventTypeId,
            Integer categoryId,
            String location,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            Integer serviceDuration,
            Double minRating,
            LocalDate serviceStartDate,
            LocalDate serviceEndDate,
            Boolean searchByAvailability
    ) {

        if (isServiceFilter == Boolean.TRUE) {
            Specification<Service> serviceSpecification = Specification.where(ServiceSpecification.hasName(name))
                    .and(ServiceSpecification.hasEventTypeId(eventTypeId))
                    .and(ServiceSpecification.hasCategoryId(categoryId))
                    .and(ServiceSpecification.hasLocation(location))
                    .and(ServiceSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ServiceSpecification.minDiscount(minDiscount))
                    .and(ServiceSpecification.minRating(minRating))
                    .and(ServiceSpecification.betweenDates(serviceStartDate, serviceEndDate))
                    .and(ServiceSpecification.hasServiceDuration(serviceDuration))
                    .and(ServiceSpecification.isAvailable(searchByAvailability));

            return serviceRepository.findAll(serviceSpecification).stream()
                    .map(this::mapToGetOfferingDTO)
                    .collect(Collectors.toList());
        } else if (isServiceFilter == Boolean.FALSE) {
            Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                    .and(ProductSpecification.hasEventTypeId(eventTypeId))
                    .and(ProductSpecification.hasCategoryId(categoryId))
                    .and(ProductSpecification.hasLocation(location))
                    .and(ProductSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ProductSpecification.minDiscount(minDiscount))
                    .and(ProductSpecification.minRating(minRating));

            return productRepository.findAll(productSpecification).stream()
                    .map(this::mapToGetOfferingDTO)
                    .collect(Collectors.toList());
        } else {
            return offeringRepository.findAll().stream()
                    .map(this::mapToGetOfferingDTO)
                    .collect(Collectors.toList());
        }
    }

    public PagedResponse<GetOfferingDTO> getAllOfferings(
            Pageable pageable,
            Boolean isServiceFilter,
            String name,
            Integer eventTypeId,
            Integer categoryId,
            String location,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            Integer serviceDuration,
            Double minRating,
            LocalDate serviceStartDate,
            LocalDate serviceEndDate,
            Boolean searchByAvailability,
            String sortBy,
            String sortDirection
    ) {
        Page<? extends Offering> pagedOfferings;

        if (isServiceFilter == Boolean.TRUE) {
            Specification<Service> serviceSpecification = Specification.where(ServiceSpecification.hasName(name))
                    .and(ServiceSpecification.hasEventTypeId(eventTypeId))
                    .and(ServiceSpecification.hasCategoryId(categoryId))
                    .and(ServiceSpecification.hasLocation(location))
                    .and(ServiceSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ServiceSpecification.minDiscount(minDiscount))
                    .and(ServiceSpecification.minRating(minRating))
                    .and(ServiceSpecification.betweenDates(serviceStartDate, serviceEndDate))
                    .and(ServiceSpecification.hasServiceDuration(serviceDuration))
                    .and(ServiceSpecification.isAvailable(searchByAvailability));

            pagedOfferings = serviceRepository.findAll(serviceSpecification, pageable);

        } else if (isServiceFilter == Boolean.FALSE) {
            Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                    .and(ProductSpecification.hasEventTypeId(eventTypeId))
                    .and(ProductSpecification.hasCategoryId(categoryId))
                    .and(ProductSpecification.hasLocation(location))
                    .and(ProductSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ProductSpecification.minDiscount(minDiscount))
                    .and(ProductSpecification.minRating(minRating));

            pagedOfferings = productRepository.findAll(productSpecification, pageable);

        } else {
            pagedOfferings = offeringRepository.findAll(pageable);
        }

        List<Offering> filteredOfferings = pagedOfferings.getContent().stream()
                .map(offering -> (Offering) offering)
                .collect(Collectors.toList());

        Comparator<Offering> comparator = getOfferingComparator(sortBy, sortDirection);
        if (comparator != null) {
            filteredOfferings = filteredOfferings.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        List<GetOfferingDTO> offeringDTOs = filteredOfferings.stream()
                .map(this::mapToGetOfferingDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(offeringDTOs, pagedOfferings.getTotalPages(), pagedOfferings.getTotalElements());
    }


    public List<GetOfferingDTO> findTopOfferings() {
        List<Offering> offerings = offeringRepository.findAll();

        return offerings.stream()
                .sorted((o1, o2) -> Double.compare(
                        calculateAverageRating(o2), calculateAverageRating(o1)))
                .limit(5)
                .map(this::mapToGetOfferingDTO)
                .collect(Collectors.toList());
    }

    // HELPER FUNCTIONS

    private GetOfferingDTO mapToGetOfferingDTO(Offering offering) {
        GetOfferingDTO dto = new GetOfferingDTO();

        dto.setId(offering.getId());
        dto.setProvider(setGetProviderDTO(offering));
        dto.setCategory(modelMapper.map(offering.getCategory(), GetOfferingCategoryDTO.class));
        dto.setAverageRating(calculateAverageRating(offering));
        if (offering.getClass().equals(Product.class)) {
            Product pr = (Product) offering;
            dto.setName(pr.getCurrentDetails().getName());
            dto.setDescription(pr.getCurrentDetails().getDescription());
            dto.setPrice(pr.getCurrentDetails().getPrice());
            dto.setDiscount(pr.getCurrentDetails().getDiscount());
            dto.setLocation(modelMapper.map(pr.getProvider().getLocation(), GetLocationDTO.class));

            dto.setProduct(true);
        }
        else{
            Service service = (Service) offering;
            dto.setName(service.getCurrentDetails().getName());
            dto.setDescription(service.getCurrentDetails().getDescription());
            dto.setPrice(service.getCurrentDetails().getPrice());
            dto.setDiscount(service.getCurrentDetails().getDiscount());
            dto.setLocation(modelMapper.map(service.getProvider().getLocation(), GetLocationDTO.class));
            dto.setSpecification(service.getCurrentDetails().getSpecification());

            dto.setProduct(false);
        }
        return dto;
    }

    private double calculateAverageRating(Offering offering) {
        if (offering.getRatings() == null || offering.getRatings().isEmpty()) {
            return 0.0;
        }
        OptionalDouble average = offering.getRatings().stream()
                .mapToInt(Rating::getScore)
                .average();

        return average.orElse(0.0);
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

    private Comparator<Offering> getOfferingComparator(String sortBy, String sortDirection) {
        Comparator<Offering> comparator = null;

        if ("price".equalsIgnoreCase(sortBy)) {
            comparator = (o1, o2) -> {
                if (o1 instanceof Service && o2 instanceof Service) {
                    return Double.compare(((Service) o1).getCurrentDetails().getPrice(), ((Service) o2).getCurrentDetails().getPrice());
                } else if (o1 instanceof Product && o2 instanceof Product) {
                    return Double.compare(((Product) o1).getCurrentDetails().getPrice(), ((Product) o2).getCurrentDetails().getPrice());
                }
                return 0;
            };
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            comparator = (o1, o2) -> {
                Double rating1 = calculateAverageRating(o1);
                Double rating2 = calculateAverageRating(o2);
                return Double.compare(rating1, rating2);
            };
        } else if ("name".equalsIgnoreCase(sortBy)) {
            comparator = (o1, o2) -> {
                if (o1 instanceof Service && o2 instanceof Service) {
                    return ((Service) o1).getCurrentDetails().getName().compareToIgnoreCase(((Service) o2).getCurrentDetails().getName());
                } else if (o1 instanceof Product && o2 instanceof Product) {
                    return ((Product) o1).getCurrentDetails().getName().compareToIgnoreCase(((Product) o2).getCurrentDetails().getName());
                }
                return 0;
            };
        }

        if ("desc".equalsIgnoreCase(sortDirection) && comparator != null) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

}
