package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetProductDTO>> getProducts() {
        Collection<GetProductDTO> products = new ArrayList<>() ;

        GetProductDTO product1 = new GetProductDTO();
        product1.setId(1);
        product1.setCategoryId(1);
        product1.setPending(false);
        product1.setProviderID(101);
        product1.setName("Premium Catering Service");
        product1.setDescription("High-quality catering for weddings and large events.");
        product1.setPrice(1200.00);
        product1.setDiscount(10.0);
        product1.setPhotos(Arrays.asList("https://example.com/photos/catering1.jpg", "https://example.com/photos/catering2.jpg"));
        product1.setVisible(true);
        product1.setAvailable(true);
        products.add(product1);

        // Product 2
        GetProductDTO product2 = new GetProductDTO();
        product2.setId(2);
        product2.setCategoryId(2);
        product2.setPending(true);
        product2.setProviderID(102);
        product2.setName("Live Music Band");
        product2.setDescription("Professional live band for corporate events and private parties.");
        product2.setPrice(2000.00);
        product2.setDiscount(5.0);
        product2.setPhotos(Arrays.asList("https://example.com/photos/music1.jpg"));
        product2.setVisible(false);
        product2.setAvailable(false);
        products.add(product2);

        // Product 3
        GetProductDTO product3 = new GetProductDTO();
        product3.setId(3);
        product3.setCategoryId(3);
        product3.setPending(false);
        product3.setProviderID(103);
        product3.setName("Deluxe Wedding Photography");
        product3.setDescription("Capture every moment with our deluxe wedding photography package.");
        product3.setPrice(3000.00);
        product3.setDiscount(15.0);
        product3.setPhotos(Arrays.asList("https://example.com/photos/photo1.jpg", "https://example.com/photos/photo2.jpg"));
        product3.setVisible(true);
        product3.setAvailable(true);
        products.add(product3);

        // Product 4
        GetProductDTO product4 = new GetProductDTO();
        product4.setId(4);
        product4.setCategoryId(4);
        product4.setPending(false);
        product4.setProviderID(104);
        product4.setName("Luxury Event Decoration");
        product4.setDescription("Top-tier decorations for upscale events.");
        product4.setPrice(2500.00);
        product4.setDiscount(0.0);
        product4.setPhotos(Arrays.asList("https://example.com/photos/decor1.jpg"));
        product4.setVisible(true);
        product4.setAvailable(false);
        products.add(product4);

        // Product 5
        GetProductDTO product5 = new GetProductDTO();
        product5.setId(5);
        product5.setCategoryId(5);
        product5.setPending(true);
        product5.setProviderID(105);
        product5.setName("Interactive DJ Service");
        product5.setDescription("Make your event unforgettable with our interactive DJ services.");
        product5.setPrice(800.00);
        product5.setDiscount(20.0);
        product5.setPhotos(Arrays.asList("https://example.com/photos/dj1.jpg", "https://example.com/photos/dj2.jpg"));
        product5.setVisible(false);
        product5.setAvailable(true);
        products.add(product5);

        return new ResponseEntity<Collection<GetProductDTO>>(products, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProductDTO> getProduct(@PathVariable("id") int id) {
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

}
