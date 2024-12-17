package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.product.*;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetProductDTO>> getProducts(
            @RequestParam(required = false) Integer providerId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minDiscount,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String name
    ) {
        List<GetProductDTO> services = productService.findAll(name,eventTypeId,categoryId,minPrice,maxPrice,isAvailable);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetProductDTO>> getProductsPage(
            SpringDataWebProperties.Pageable page,
            @RequestParam(required = false) Integer providerId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minDiscount,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String name
    ) {
        PagedResponse<GetProductDTO> response = productService.findAll(page, name, categoryId, eventTypeId, minPrice, maxPrice, isAvailable);
        return new ResponseEntity<PagedResponse<GetProductDTO>>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProductDTO> getProduct(@PathVariable("id") int id, @RequestParam(required = false) LocalDateTime historyTimestamp) {
        GetProductDTO product = new GetProductDTO();
        product.setId(id);
        product.setCategoryId(1);
        product.setPending(false);
        product.setProviderID(104);
        product.setName("Luxury Event Decoration");
        product.setDescription("Top-tier decorations for upscale events.");
        product.setPrice(2500.00);
        product.setDiscount(0.0);
        product.setPhotos(Arrays.asList("https://example.com/photos/decor1.jpg"));
        product.setVisible(true);
        product.setAvailable(false);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedProductDTO> createProduct(@RequestBody CreateProductDTO product) throws Exception {
        CreatedProductDTO createdProduct = new CreatedProductDTO();
        createdProduct.setId(1);
        createdProduct.setCategoryId(product.getCategoryId());
        createdProduct.setPending(product.isPending());
        createdProduct.setProviderID(product.getProviderID());
        createdProduct.setName(product.getName());
        createdProduct.setDescription(product.getDescription());
        createdProduct.setPrice(product.getPrice());
        createdProduct.setDiscount(product.getDiscount());
        createdProduct.setPhotos(product.getPhotos());
        createdProduct.setVisible(product.isVisible());
        createdProduct.setAvailable(product.isAvailable());

        return new ResponseEntity<CreatedProductDTO>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedProductDTO> updateProduct(@RequestBody UpdateProductDTO product, @PathVariable("id") int id) {
        UpdatedProductDTO updatedProduct = new UpdatedProductDTO();
        updatedProduct.setId(1);
        updatedProduct.setName(product.getName());
        updatedProduct.setDescription(product.getDescription());
        updatedProduct.setPrice(product.getPrice());
        updatedProduct.setDiscount(product.getDiscount());
        updatedProduct.setPhotos(product.getPhotos());
        updatedProduct.setVisible(product.isVisible());
        updatedProduct.setAvailable(product.isAvailable());

        return new ResponseEntity<UpdatedProductDTO>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") int id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping(value = "/{id}/buy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buyProduct(@PathVariable("id") int productId) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
