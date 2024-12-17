package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatePricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.product.GetProductDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.dto.service.UpdatedServiceDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.Product;
import com.ftn.iss.eventPlanner.model.ProductDetails;
import com.ftn.iss.eventPlanner.model.ServiceDetails;
import com.ftn.iss.eventPlanner.model.specification.ProductSpecification;
import com.ftn.iss.eventPlanner.model.specification.ServiceSpecification;
import com.ftn.iss.eventPlanner.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    public List<GetProductDTO> findAll(
            String name,
            Integer eventTypeId,
            Integer categoryId,
            Double minPrice,
            Double maxPrice,
            Boolean searchByAvailability
    ) {
        // TODO: add whats needed for filtering
        Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasEventTypeId(eventTypeId))
                .and(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.betweenPrices(minPrice, maxPrice));

        return productRepository.findAll(productSpecification).stream()
                .map(product -> modelMapper.map(product, GetProductDTO.class))
                .collect(Collectors.toList());
    }
    public PagedResponse<GetProductDTO> findAll(
            Pageable pagable,
            String name,
            Integer eventTypeId,
            Integer categoryId,
            Double minPrice,
            Double maxPrice,
            Boolean searchByAvailability
    ) {
        Specification<Product> productSpecification = Specification.where(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasEventTypeId(eventTypeId))
                .and(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.betweenPrices(minPrice, maxPrice));

        Page<Product> pagedProducts = productRepository.findAll(productSpecification, pagable);

        List<GetProductDTO> productDTOs = pagedProducts.getContent().stream()
                .map(this::mapToGetProductDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(productDTOs,pagedProducts.getTotalPages(),pagedProducts.getTotalElements());
    }
    public GetProductDTO findById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + id + " not found"));
        return mapToGetProductDTO(product);
    }
    public UpdatedServiceDTO updatePrice(int id, UpdatePricelistItemDTO updateServiceDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));

        ProductDetails newCurrent = new ProductDetails();
        newCurrent.setName(product.getCurrentDetails().getName());
        newCurrent.setDescription(product.getCurrentDetails().getDescription());
        newCurrent.setPrice(updateServiceDTO.getPrice());
        newCurrent.setDiscount(updateServiceDTO.getDiscount());

        newCurrent.setPhotos(
                product.getCurrentDetails().getPhotos() != null
                        ? new ArrayList<>(product.getCurrentDetails().getPhotos())
                        : new ArrayList<>()
        );

        newCurrent.setVisible(product.getCurrentDetails().isVisible());
        newCurrent.setAvailable(product.getCurrentDetails().isAvailable());
        newCurrent.setTimestamp(LocalDateTime.now());

        ProductDetails historicalDetails = product.getCurrentDetails();
        product.getDetailsHistory().add(historicalDetails);

        product.setCurrentDetails(newCurrent);

        Product productSaved = productRepository.save(product);

        return modelMapper.map(productSaved, UpdatedServiceDTO.class);
    }
    private GetProductDTO mapToGetProductDTO(Product product) {
        GetProductDTO dto = new GetProductDTO();

        dto.setId(product.getId());
        dto.setCategory(modelMapper.map(product.getCategory(), GetOfferingCategoryDTO.class));
        dto.setPending(product.isPending());
        dto.setProvider(setGetProviderDTO(product));
        dto.setName(product.getCurrentDetails().getName());
        dto.setDescription(product.getCurrentDetails().getDescription());
        dto.setPrice(product.getCurrentDetails().getPrice());
        dto.setDiscount(product.getCurrentDetails().getDiscount());
        dto.setPhotos(product.getCurrentDetails().getPhotos());
        dto.setAvailable(product.getCurrentDetails().isAvailable());
        dto.setDeleted(product.isDeleted());
        dto.setVisible(product.getCurrentDetails().isVisible());
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
