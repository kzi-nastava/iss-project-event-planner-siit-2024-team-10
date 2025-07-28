package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatePricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatedPricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.product.*;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.dto.service.UpdatedServiceDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.model.specification.ProductSpecification;
import com.ftn.iss.eventPlanner.model.specification.ServiceSpecification;
import com.ftn.iss.eventPlanner.repositories.AccountRepository;
import com.ftn.iss.eventPlanner.repositories.OfferingCategoryRepository;
import com.ftn.iss.eventPlanner.repositories.ProductRepository;
import com.ftn.iss.eventPlanner.repositories.ProviderRepository;
import jdk.jfr.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OfferingCategoryRepository offeringCategoryRepository;
    @Autowired
    private ProviderRepository providerRepository;
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
    public UpdatedPricelistItemDTO updatePrice(int id, UpdatePricelistItemDTO updatePricelistItemDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with ID " + id + " not found"));

        UpdateProductDTO productDTO = new UpdateProductDTO();
        productDTO.setPrice(updatePricelistItemDTO.getPrice());
        productDTO.setPhotos(product.getCurrentDetails().getPhotos());
        productDTO.setName(product.getCurrentDetails().getName());
        productDTO.setDiscount(updatePricelistItemDTO.getDiscount());
        productDTO.setAvailable(product.getCurrentDetails().isAvailable());
        productDTO.setVisible(product.getCurrentDetails().isVisible());
        productDTO.setDescription(product.getCurrentDetails().getDescription());

        update(product.getId(),productDTO);

        UpdatedPricelistItemDTO dto = new UpdatedPricelistItemDTO();
        dto.setId(product.getId());
        dto.setPrice(updatePricelistItemDTO.getPrice());
        dto.setOfferingId(product.getId());
        dto.setDiscount(updatePricelistItemDTO.getDiscount());
        dto.setName(product.getCurrentDetails().getName());
        return dto;
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

    public CreatedProductDTO create(CreateProductDTO productDTO){
        Product product = new Product();
        product.setPending(false);
        if(productDTO.getCategoryId() == 0){
            if(productDTO.getCategoryProposalName()==null)
                throw new IllegalArgumentException("Category proposal is required");
            OfferingCategory category = new OfferingCategory();
            category.setName(productDTO.getCategoryProposalName());
            category.setDescription(productDTO.getCategoryProposalDescription());
            category.setPending(true);
            category.setCreatorId(accountRepository.findByUserId(productDTO.getProviderID()).get().getId());
            category=offeringCategoryRepository.save(category);
            product.setCategory(category);
            product.setPending(true);
        }
        else{
            OfferingCategory category = offeringCategoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category with ID " + productDTO.getCategoryId() + " not found"));
            product.setCategory(category);
        }
        Provider provider = providerRepository.findById(productDTO.getProviderID())
                .orElseThrow(() -> new IllegalArgumentException("Provider with ID " + productDTO.getProviderID() + " not found"));
        product.setProvider(provider);

        ProductDetails productDetails = new ProductDetails();
        productDetails.setName(productDTO.getName());
        productDetails.setDescription(productDTO.getDescription());
        productDetails.setPrice(productDTO.getPrice());
        productDetails.setDiscount(productDTO.getDiscount());
        productDetails.setPhotos(productDTO.getPhotos());
        productDetails.setVisible(productDTO.isVisible());
        productDetails.setAvailable(productDTO.isAvailable());
        productDetails.setTimestamp(LocalDateTime.now());

        product.setCurrentDetails(productDetails);
        product = productRepository.save(product);

        CreatedProductDTO createdProductDTO = modelMapper.map(productDetails, CreatedProductDTO.class);
        createdProductDTO.setId(product.getId());
        createdProductDTO.setCategoryId(product.getCategory().getId());
        createdProductDTO.setProviderID(product.getProvider().getId());
        createdProductDTO.setPending(product.isPending());

        return createdProductDTO;
    }

    public UpdatedProductDTO update(int productId, UpdateProductDTO updateProductDTO){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));

        // Create a copy of current details before adding to history
        ProductDetails historicalDetails = new ProductDetails();
        BeanUtils.copyProperties(product.getCurrentDetails(), historicalDetails);

        product.getProductDetailsHistory().add(historicalDetails);
        modelMapper.map(updateProductDTO, product.getCurrentDetails());
        product.getCurrentDetails().setTimestamp(LocalDateTime.now());
        product.getCurrentDetails().setId(0);

        return modelMapper.map(productRepository.save(product).getCurrentDetails(), UpdatedProductDTO.class);
    }

    public void delete(int productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }
}
