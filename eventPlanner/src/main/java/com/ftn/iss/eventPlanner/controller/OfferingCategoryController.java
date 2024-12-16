package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.offeringcategory.*;
import com.ftn.iss.eventPlanner.services.OfferingCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/categories")
public class OfferingCategoryController {
    @Autowired
    private OfferingCategoryService offeringCategoryService;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetOfferingCategoryDTO>> getCategories(){
        List<GetOfferingCategoryDTO> categories = offeringCategoryService.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetOfferingCategoryDTO> getCategory(@PathVariable("id") int id) {
        try {
            GetOfferingCategoryDTO offeringCategoryDTO = offeringCategoryService.findById(id);
            return new ResponseEntity<>(offeringCategoryDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedOfferingCategoryDTO> createCategory(@Valid @RequestBody CreateOfferingCategoryDTO category) throws Exception {
        try{
            CreatedOfferingCategoryDTO createdOfferingCategoryDTO = offeringCategoryService.create(category);
            return new ResponseEntity<>(createdOfferingCategoryDTO, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedOfferingCategoryDTO> updateCategory(@RequestBody UpdateOfferingCategoryDTO category, @PathVariable int id)
            throws Exception {
        try{
            UpdatedOfferingCategoryDTO updatedOfferingCategoryDTO = offeringCategoryService.update(id,category);
            return new ResponseEntity<>(updatedOfferingCategoryDTO, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }}

    /*
    TODO: delete only if there are no offerings
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        try {
            offeringCategoryService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable int id) {
        try {
            offeringCategoryService.approve(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
