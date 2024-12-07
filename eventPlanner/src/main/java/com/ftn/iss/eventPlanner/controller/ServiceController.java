package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.eventPlanner.dto.service.*;
import com.ftn.iss.eventPlanner.services.ServiceService;
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
        List<GetServiceDTO> services = serviceService.findAll();
        return new ResponseEntity<>(services, HttpStatus.OK);
    }
    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<GetServiceDTO>> getServicesPage(
            SpringDataWebProperties.Pageable page,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer eventTypeId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String name
    ) {
        Collection<GetServiceDTO> services = new ArrayList<>() ;

        PagedResponse<GetServiceDTO> response = new PagedResponse<>(
                services,
                1,
                5
        );

        return new ResponseEntity<PagedResponse<GetServiceDTO>>(response, HttpStatus.OK);
    }
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetServiceDTO> getService(@PathVariable("id") int id, @RequestParam(required = false) LocalDateTime historyTimestamp) {
		GetServiceDTO service = new GetServiceDTO();

        service.setId(id);
        service.setCategoryId(5);
        service.setPending(false);
        service.setProviderID(5);
        service.setName("Interactive DJ Service");
        service.setDescription("Make your party unforgettable with our skilled DJ.");
        service.setSpecification("Custom playlists and top-notch audio equipment.");
        service.setPrice(2500);
        service.setDiscount(20);
        service.setPhotos(Arrays.asList("https://example.com/photos/dj1.jpg", "https://example.com/photos/dj2.jpg"));
        service.setVisible(true);
        service.setAvailable(true);
        service.setMaxDuration(6);
        service.setMinDuration(3);
        service.setCancellationPeriod(36);
        service.setReservationPeriod(48);
        service.setAutoConfirm(false);
		
        return new ResponseEntity<GetServiceDTO>(service, HttpStatus.OK);
	}
	
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatedServiceDTO> createService(@RequestBody CreateServiceDTO service) throws Exception {
        CreatedServiceDTO createdService = new CreatedServiceDTO();

        createdService.setId(service.getId());
	    createdService.setName(service.getName());
	    createdService.setDescription(service.getDescription());
	    createdService.setSpecification(service.getSpecification());
	    createdService.setPrice(service.getPrice());
	    createdService.setPhotos(service.getPhotos());
	    createdService.setDiscount(service.getDiscount());
		createdService.setVisible(service.isVisible());
		createdService.setAvailable(service.isAvailable());
		createdService.setMaxDuration(service.getMaxDuration());
		createdService.setMinDuration(service.getMinDuration());
		createdService.setCancellationPeriod(service.getCancellationPeriod());
		createdService.setReservationPeriod(service.getReservationPeriod());
		createdService.setVisible(service.isVisible());
		createdService.setAvailable(service.isAvailable());
		createdService.setAutoConfirm(service.isAutoConfirm());
		createdService.setCategoryId(service.getCategoryId());
		createdService.setPending(service.isPending());
		createdService.setProviderID(service.getProviderID());
        return new ResponseEntity<CreatedServiceDTO>(createdService, HttpStatus.CREATED);
    }
	
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UpdatedServiceDTO> updateService(@RequestBody UpdateServiceDTO service, @PathVariable int id)
			throws Exception {
		UpdatedServiceDTO updatedService = new UpdatedServiceDTO();

        updatedService.setId(id);
        updatedService.setName(service.getName());
        updatedService.setDescription(service.getDescription());
        updatedService.setSpecification(service.getSpecification());
        updatedService.setPhotos(service.getPhotos());
        updatedService.setPrice(service.getPrice());
        updatedService.setDiscount(service.getDiscount());
        updatedService.setVisible(service.isVisible());
        updatedService.setAvailable(service.isAvailable());
        updatedService.setMaxDuration(service.getMaxDuration());
        updatedService.setMinDuration(service.getMinDuration());
        updatedService.setCancellationPeriod(service.getCancellationPeriod());
        updatedService.setReservationPeriod(service.getReservationPeriod());
		

		return new ResponseEntity<UpdatedServiceDTO>(updatedService, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") int id) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
