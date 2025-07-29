package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.product.*;
import com.ftn.iss.eventPlanner.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProductDTO> getProduct(@PathVariable("id") int id, @RequestParam(required = false) LocalDateTime historyTimestamp) {
        GetProductDTO serviceDTO = productService.findById(id);
        return new ResponseEntity<>(serviceDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedProductDTO> createProduct(@RequestBody @Valid CreateProductDTO product) throws Exception {
        CreatedProductDTO createdProduct = productService.create(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedProductDTO> updateProduct(@RequestBody @Valid UpdateProductDTO product, @PathVariable("id") int id) {
        UpdatedProductDTO updatedProduct = productService.update(id, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('PROVIDER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") int id) {
        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
