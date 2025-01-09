package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.model.specification.ProductSpecification;
import com.ftn.iss.eventPlanner.model.specification.ServiceSpecification;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import com.ftn.iss.eventPlanner.repositories.ProductRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;


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
    @Autowired
    private DTOMapper dtoMapper;

    public List<GetOfferingDTO> findAll(){
        List<Offering> offerings = offeringRepository.findAll();

        return offerings.stream()
                .map(dtoMapper::mapToGetOfferingDTO)
                .collect(Collectors.toList());
    }

    public List<GetOfferingDTO> getAllOfferings(
            Boolean isServiceFilter,
            String name,
            Integer categoryId,
            String location,
            Double minPrice,
            Double maxPrice,
            Integer minDiscount,
            Integer serviceDuration,
            Double minRating,
            Boolean searchByAvailability
    ) {

        if (isServiceFilter == Boolean.TRUE) {
            Specification<Service> serviceSpecification = Specification.where(ServiceSpecification.hasName(name))
                    .and(ServiceSpecification.hasCategoryId(categoryId))
                    .and(ServiceSpecification.hasLocation(location))
                    .and(ServiceSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ServiceSpecification.minDiscount(minDiscount))
                    .and(ServiceSpecification.minRating(minRating))
                    .and(ServiceSpecification.hasServiceDuration(serviceDuration))
                    .and(ServiceSpecification.isAvailable(searchByAvailability));

            return serviceRepository.findAll(serviceSpecification).stream()
                    .map(dtoMapper::mapToGetOfferingDTO)
                    .collect(Collectors.toList());
        } else if (isServiceFilter == Boolean.FALSE) {
            Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                    .and(ProductSpecification.hasCategoryId(categoryId))
                    .and(ProductSpecification.hasLocation(location))
                    .and(ProductSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ProductSpecification.minDiscount(minDiscount))
                    .and(ProductSpecification.minRating(minRating))
                    .and(ProductSpecification.isAvailable(searchByAvailability));

            return productRepository.findAll(productSpecification).stream()
                    .map(dtoMapper::mapToGetOfferingDTO)
                    .collect(Collectors.toList());
        } else {
            return offeringRepository.findAll().stream()
                    .map(dtoMapper::mapToGetOfferingDTO)
                    .collect(Collectors.toList());
        }
    }

    public PagedResponse<GetOfferingDTO> getAllOfferings(
            Pageable pageable,
            Boolean isServiceFilter,
            String name,
            Integer categoryId,
            String location,
            Double minPrice,
            Double maxPrice,
            Integer minDiscount,
            Integer serviceDuration,
            Double minRating,
            Boolean searchByAvailability,
            String sortBy,
            String sortDirection
    ) {
        if (sortBy != null && !"none".equalsIgnoreCase(sortBy)) {
            String sortField = switch (sortBy.toLowerCase()) {
                case "price" -> "currentDetails.price";
                case "averageRating" -> null; // Dynamically sorted
                case "name" -> "currentDetails.name";
                case "location.city" -> "provider.location.city";
                default -> null;
            };

            if (sortField != null) {
                var sortDirectionEnum = "desc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(sortDirectionEnum, sortField));
            }
        }

        Page<? extends Offering> pagedOfferings;
        if (isServiceFilter == Boolean.TRUE) {
            Specification<Service> serviceSpecification = Specification.where(ServiceSpecification.hasName(name))
                    .and(ServiceSpecification.hasCategoryId(categoryId))
                    .and(ServiceSpecification.hasLocation(location))
                    .and(ServiceSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ServiceSpecification.minDiscount(minDiscount))
                    .and(ServiceSpecification.minRating(minRating))
                    .and(ServiceSpecification.hasServiceDuration(serviceDuration))
                    .and(ServiceSpecification.isAvailable(searchByAvailability));

            pagedOfferings = serviceRepository.findAll(serviceSpecification, pageable);

        } else if (isServiceFilter == Boolean.FALSE) {
            Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                    .and(ProductSpecification.hasCategoryId(categoryId))
                    .and(ProductSpecification.hasLocation(location))
                    .and(ProductSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ProductSpecification.minDiscount(minDiscount))
                    .and(ProductSpecification.minRating(minRating))
                    .and(ProductSpecification.isAvailable(searchByAvailability));

            pagedOfferings = productRepository.findAll(productSpecification, pageable);

        } else {
            pagedOfferings = offeringRepository.findAll(pageable);
        }

        List<Offering> filteredOfferings = pagedOfferings.getContent().stream()
                .map(offering -> (Offering) offering)
                .collect(Collectors.toList());
        if ("averageRating".equalsIgnoreCase(sortBy)) {
            filteredOfferings.sort(getOfferingComparator(sortDirection));
        }

        List<GetOfferingDTO> offeringDTOs = filteredOfferings.stream()
                .map(dtoMapper::mapToGetOfferingDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(offeringDTOs, pagedOfferings.getTotalPages(), pagedOfferings.getTotalElements());
    }

    public List<GetOfferingDTO> findTopOfferings() {
        List<Offering> offerings = offeringRepository.findAll();

        return offerings.stream()
                .sorted((o1, o2) -> Double.compare(
                        dtoMapper.calculateAverageRating(o2), dtoMapper.calculateAverageRating(o1)))
                .limit(5)
                .map(dtoMapper::mapToGetOfferingDTO)
                .collect(Collectors.toList());
    }


    private Comparator<Offering> getOfferingComparator(String sortDirection) {
        Comparator<Offering> comparator;
        comparator = (o1, o2) -> {
            Double rating1 = dtoMapper.calculateAverageRating(o1);
            Double rating2 = dtoMapper.calculateAverageRating(o2);
            return Double.compare(rating1, rating2);
        };

        if ("desc".equalsIgnoreCase(sortDirection) && comparator != null) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    public Double getHighestPrice(Boolean isService) {
        try {
            Double highestPrice = null;
            if (Boolean.FALSE.equals(isService)) {
                highestPrice = productRepository.findMaxProductPrice();
            } else if (Boolean.TRUE.equals(isService)) {
                highestPrice = serviceRepository.findMaxServicePrice();
            } else {
                throw new IllegalArgumentException("Invalid isService parameter. It must be true or false.");
            }

            if (highestPrice == null) {
                throw new NoSuchElementException("No prices found for the specified filter.");
            }

            return highestPrice;
        } catch (Exception e) {
            System.err.println("Error while fetching the highest price: " + e.getMessage());
            throw e;
        }
    }

}
