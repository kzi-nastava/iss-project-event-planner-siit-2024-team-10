package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.service.*;
import com.ftn.iss.eventPlanner.services.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetServiceDTO>> getServices(
        @RequestParam(required = false) Integer categoryId,
        @RequestParam(required = false) Integer eventTypeId,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) Boolean isAvailable,
        @RequestParam(required = false) String name
    ){
        List<GetServiceDTO> services = serviceService.findAll(name,eventTypeId,categoryId,minPrice,maxPrice,isAvailable);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }
    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetServiceDTO>> getServices(
            Pageable pageable,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String name
    ) {
        PagedResponse<GetServiceDTO> response = serviceService.findAll(pageable, name, categoryId, eventTypeId, minPrice, maxPrice, isAvailable);
        return new ResponseEntity<PagedResponse<GetServiceDTO>>(response, HttpStatus.OK);
    }
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetServiceDTO> getService(@PathVariable("id") int id, @RequestParam(required = false) LocalDateTime historyTimestamp) {
        try {
            GetServiceDTO serviceDTO = serviceService.findById(id);
            return new ResponseEntity<>(serviceDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
	}
	
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedServiceDTO> createService(@Valid @RequestBody CreateServiceDTO service) throws Exception {
        try{
            CreatedServiceDTO createdServiceDTO = serviceService.create(service);
            return new ResponseEntity<>(createdServiceDTO, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
	
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UpdatedServiceDTO> updateService(@RequestBody UpdateServiceDTO service, @PathVariable int id)
			throws Exception {
        try{
            UpdatedServiceDTO updatedServiceDTO = serviceService.update(id,service);
            return new ResponseEntity<>(updatedServiceDTO, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") int id) {
        try {
            serviceService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
	}
}
