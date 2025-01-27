package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;


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

    public List<GetOfferingDTO> findAll() {
        List<Offering> offerings = offeringRepository.findAll().stream()
                .filter(offering -> {
                    if (offering instanceof Product) {
                        return ((Product) offering).getCurrentDetails().isVisible();
                    } else if (offering instanceof Service) {
                        return ((Service) offering).getCurrentDetails().isVisible();
                    }
                    return false;
                })
                .collect(Collectors.toList());

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
    @Transactional(readOnly = true)
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
            String sortDirection,
            Integer accountId
    ) {
        if (accountId != null && (location == null || location.isEmpty())) {
            Location userLocation = accountService.findUserLocation(accountId);
            if (userLocation != null) {
                location = userLocation.getCity();
            }
        }

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
                    .and(ServiceSpecification.isAvailable(searchByAvailability))
                    .and(ServiceSpecification.isVisible());

            pagedOfferings = serviceRepository.findAll(serviceSpecification, pageable);

        } else if (isServiceFilter == Boolean.FALSE) {
            Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                    .and(ProductSpecification.hasCategoryId(categoryId))
                    .and(ProductSpecification.hasLocation(location))
                    .and(ProductSpecification.betweenPrices(minPrice, maxPrice))
                    .and(ProductSpecification.minDiscount(minDiscount))
                    .and(ProductSpecification.minRating(minRating))
                    .and(ProductSpecification.isAvailable(searchByAvailability))
                    .and(ProductSpecification.isVisible());

            pagedOfferings = productRepository.findAll(productSpecification, pageable);

        } else {
            Specification<Offering> visibilitySpec = (root, query, cb) -> {
                Subquery<Product> productSubquery = query.subquery(Product.class);
                Root<Product> productRoot = productSubquery.from(Product.class);
                productSubquery.select(productRoot).where(
                        cb.and(
                                cb.equal(productRoot.get("id"), root.get("id")),
                                cb.isTrue(productRoot.get("currentDetails").get("isVisible"))
                        )
                );

                Subquery<Service> serviceSubquery = query.subquery(Service.class);
                Root<Service> serviceRoot = serviceSubquery.from(Service.class);
                serviceSubquery.select(serviceRoot).where(
                        cb.and(
                                cb.equal(serviceRoot.get("id"), root.get("id")),
                                cb.isTrue(serviceRoot.get("currentDetails").get("isVisible"))
                        )
                );

                return cb.or(
                        cb.and(cb.equal(root.type(), Product.class), cb.exists(productSubquery)),
                        cb.and(cb.equal(root.type(), Service.class), cb.exists(serviceSubquery))
                );
            };
            pagedOfferings = offeringRepository.findAll(visibilitySpec, pageable);
        }

        List<Offering> filteredOfferings = pagedOfferings.getContent().stream()
                .map(offering -> (Offering) offering)
                .filter(offering -> {
                    if (offering instanceof Product) {
                        Product product = (Product) offering;
                        return product.getCurrentDetails() != null && product.getCurrentDetails().isVisible();
                    } else if (offering instanceof Service) {
                        Service service = (Service) offering;
                        return service.getCurrentDetails() != null && service.getCurrentDetails().isVisible();
                    }
                    return false;
                })
                .collect(Collectors.toList());

        if ("averageRating".equalsIgnoreCase(sortBy)) {
            filteredOfferings.sort(getOfferingComparator(sortDirection));
        }

        List<GetOfferingDTO> offeringDTOs = filteredOfferings.stream()
                .map(dtoMapper::mapToGetOfferingDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(offeringDTOs, pagedOfferings.getTotalPages(), pagedOfferings.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<GetOfferingDTO> findTopOfferings(Integer accountId) {
        String location = null;

        if (accountId != null) {
            Location userLocation = accountService.findUserLocation(accountId);
            if (userLocation != null) {
                location = userLocation.getCity();
            }
        }

        final String finalLocation = location;

        List<Offering> offerings = offeringRepository.findAll().stream()
                .filter(offering -> {
                    boolean isVisible = false;
                    if (offering instanceof Product) {
                        isVisible = ((Product) offering).getCurrentDetails().isVisible();
                    } else if (offering instanceof Service) {
                        isVisible = ((Service) offering).getCurrentDetails().isVisible();
                    }

                    if (finalLocation != null && isVisible) {
                        if (offering instanceof Product) {
                            return finalLocation.equals(((Product) offering).getProvider().getLocation().getCity());
                        } else if (offering instanceof Service) {
                            return finalLocation.equals(((Service) offering).getProvider().getLocation().getCity());
                        }
                    }

                    return isVisible;
                })
                .collect(Collectors.toList());

        // If no offerings found for the user's city, display all visible offerings
        if (offerings.isEmpty() && finalLocation != null) {
            offerings = offeringRepository.findAll().stream()
                    .filter(offering -> {
                        if (offering instanceof Product) {
                            return ((Product) offering).getCurrentDetails().isVisible();
                        } else if (offering instanceof Service) {
                            return ((Service) offering).getCurrentDetails().isVisible();
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        return offerings.stream()
                .sorted((o1, o2) -> Double.compare(
                        calculateAverageRating(o2), calculateAverageRating(o1)))
                .limit(5)
                .map(this::mapToGetOfferingDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<GetCommentDTO> getComments(int offeringId) {
        Optional<Offering> offering = offeringRepository.findById(offeringId);

        if (offering.isPresent()) {
            return offering.get().getComments().stream()
                    .filter(comment -> comment.getStatus() != Status.PENDING)
                    .map(this::mapToGetCommentDTO)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
    @Transactional
    public List<GetOfferingDTO> getOfferingsByProviderId(int providerId) {
        return offeringRepository.findAll().stream()
                .filter(offering -> offering.getProvider().getId() == providerId)
                .filter(offering -> {
                    if (offering instanceof Product) {
                        return ((Product) offering).getCurrentDetails().isVisible();
                    } else if (offering instanceof Service) {
                        return ((Service) offering).getCurrentDetails().isVisible();
                    }
                    return false;
                })
                .map(this::mapToGetOfferingDTO)
                .collect(Collectors.toList());
    }
    private GetCommentDTO mapToGetCommentDTO(Comment comment) {
        return new GetCommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getStatus(),
                comment.getCommenter().getId(),
                comment.getRating(),
                comment.getCommenter().getUsername()
        );
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

    private Comparator<Offering> getOfferingComparator(String sortDirection) {
        Comparator<Offering> comparator;
        comparator = (o1, o2) -> {
            Double rating1 = calculateAverageRating(o1);
            Double rating2 = calculateAverageRating(o2);
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
