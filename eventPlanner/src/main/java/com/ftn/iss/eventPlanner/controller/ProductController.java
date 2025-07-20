package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.product.*;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            Pageable page,
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
        PagedResponse<GetProductDTO> response = productService.findAll(page,name,eventTypeId,categoryId,minPrice,maxPrice,isAvailable);
        return new ResponseEntity<PagedResponse<GetProductDTO>>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProductDTO> getProduct(@PathVariable("id") int id, @RequestParam(required = false) LocalDateTime historyTimestamp) {
        try {
            GetProductDTO serviceDTO = productService.findById(id);
            return new ResponseEntity<>(serviceDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedProductDTO> createProduct(@RequestBody @Valid CreateProductDTO product) throws Exception {
        CreatedProductDTO createdProduct = productService.create(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedProductDTO> updateProduct(@RequestBody UpdateProductDTO product, @PathVariable("id") int id) {
        UpdatedProductDTO updatedProduct = productService.update(id, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
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
