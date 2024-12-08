package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingCardDTO;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.Product;
import com.ftn.iss.eventPlanner.model.specification.ProductSpecification;
import com.ftn.iss.eventPlanner.model.specification.ServiceSpecification;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import com.ftn.iss.eventPlanner.repositories.ProductRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.ftn.iss.eventPlanner.model.Service;
import com.ftn.iss.eventPlanner.model.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
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


    public List<GetOfferingCardDTO> findAll(){
        List<Offering> offerings = offeringRepository.findAll();

        return offerings.stream()
                .map(this::mapToGetOfferingCardDTO)
                .collect(Collectors.toList());
    }

    public List<GetOfferingCardDTO> getAllOfferings(
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
                    .map(this::mapToGetOfferingCardDTO)
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
                    .map(this::mapToGetOfferingCardDTO)
                    .collect(Collectors.toList());
        } else {
            return offeringRepository.findAll().stream()
                    .map(this::mapToGetOfferingCardDTO)
                    .collect(Collectors.toList());
        }
    }

    public PagedResponse<GetOfferingCardDTO> getAllOfferings(
            Pageable pagable,
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

            Page<Service> pagedOfferings = serviceRepository.findAll(serviceSpecification, pagable);

            List<GetOfferingCardDTO> offeringDTOs = pagedOfferings.getContent().stream()
                    .map(this::mapToGetOfferingCardDTO)
                    .collect(Collectors.toList());

            return new PagedResponse<>(offeringDTOs,pagedOfferings.getTotalPages(),pagedOfferings.getTotalElements());
        } else if (isServiceFilter == Boolean.FALSE) {
            Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                    .and(ProductSpecification.hasEventTypeId(eventTypeId))
                    .and(ProductSpecification.hasCategoryId(categoryId))
                    .and(ProductSpecification.hasLocation(location))
                    .and(ProductSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ProductSpecification.minDiscount(minDiscount))
                    .and(ProductSpecification.minRating(minRating));

            Page<Product> pagedOfferings = productRepository.findAll(productSpecification, pagable);

            List<GetOfferingCardDTO> offeringDTOs = pagedOfferings.getContent().stream()
                    .map(this::mapToGetOfferingCardDTO)
                    .collect(Collectors.toList());

            return new PagedResponse<>(offeringDTOs,pagedOfferings.getTotalPages(),pagedOfferings.getTotalElements());
        } else {
            Page<Offering> pagedOfferings = offeringRepository.findAll(pagable);

            List<GetOfferingCardDTO> offeringDTOs = pagedOfferings.getContent().stream()
                    .map(this::mapToGetOfferingCardDTO)
                    .collect(Collectors.toList());

            return new PagedResponse<>(offeringDTOs,pagedOfferings.getTotalPages(),pagedOfferings.getTotalElements());
        }
    }


    // HELPER FUNCTIONS

    private GetOfferingCardDTO mapToGetOfferingCardDTO(Offering offering) {
        GetOfferingCardDTO dto = new GetOfferingCardDTO();
        dto.setId(offering.getId());
        dto.setName(offering.getProvider().getFirstName()+" "+offering.getProvider().getLastName());
        dto.setCategory(offering.getCategory().getName());
        dto.setAverageRating(calculateAverageRating(offering));

        if (offering.getClass().equals(Product.class)) {
            Product pr = (Product) offering;
            dto.setName(pr.getCurrentDetails().getName());
            dto.setPrice(pr.getCurrentDetails().getPrice());

            // TO BE CHANGED WHEN PHOTOS ATTRIBUTE IS CHANGED TO A SET
            Set<String> photos = pr.getCurrentDetails().getPhotos();
            if (photos != null && !photos.isEmpty()) {
                String coverPicture = new ArrayList<>(photos).getFirst();
                dto.setCoverPicture(coverPicture);
            }
            dto.setIsService(false);
        }
        else{
            Service service = (Service) offering;
            dto.setName(service.getCurrentDetails().getName());
            dto.setPrice(service.getCurrentDetails().getPrice());
            List<String> photos = service.getCurrentDetails().getPhotos();
            if (photos != null && !photos.isEmpty()) {
                String coverPicture = new ArrayList<>(photos).getFirst();
                dto.setCoverPicture(coverPicture);
            }
            dto.setIsService(true);
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
}
