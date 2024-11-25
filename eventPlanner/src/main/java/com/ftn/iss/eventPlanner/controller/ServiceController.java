package com.ftn.iss.eventPlanner.controller;

import com.ftn.iss.eventPlanner.dto.*;
import com.ftn.iss.primjerZaPrvuKT.controller.Long;
import com.ftn.iss.primjerZaPrvuKT.dto.GetBookDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetServiceDTO>> getServices() {
        Collection<GetServiceDTO> services = new ArrayList<>();

        // Service 1
        GetServiceDTO service1 = new GetServiceDTO();
        service1.setId(1);
        service1.setCategoryId(6);
        service1.setPending(true);
        service1.setProviderID(1);
        service1.setName("Bridal Makeup");
        service1.setDescription("Beautiful bridal makeup for the bride and her party.");
        service1.setSpecification("We use the best products for long-lasting results.");
        service1.setPrice(2000);
        service1.setDiscount(0);
        service1.setPhotos(Arrays.asList("https://example.com/photos/makeup1.jpg", "https://example.com/photos/makeup2.jpg"));
        service1.setVisible(true);
        service1.setAvailable(true);
        service1.setMaxDuration(4);
        service1.setMinDuration(2);
        service1.setCancellationPeriod(24);
        service1.setReservationPeriod(48);
        service1.setAutoConfirm(false);
        services.add(service1);

        // Service 2
        GetServiceDTO service2 = new GetServiceDTO();
        service2.setId(2);
        service2.setCategoryId(3);
        service2.setPending(false);
        service2.setProviderID(2);
        service2.setName("Wedding Photography");
        service2.setDescription("Capture your special moments with our expert photographers.");
        service2.setSpecification("Includes full-day coverage and edited photos.");
        service2.setPrice(5000);
        service2.setDiscount(10);
        service2.setPhotos(Arrays.asList("https://example.com/photos/photo1.jpg", "https://example.com/photos/photo2.jpg"));
        service2.setVisible(true);
        service2.setAvailable(true);
        service2.setMaxDuration(8);
        service2.setMinDuration(6);
        service2.setCancellationPeriod(48);
        service2.setReservationPeriod(72);
        service2.setAutoConfirm(true);
        services.add(service2);

        // Service 3
        GetServiceDTO service3 = new GetServiceDTO();
        service3.setId(3);
        service3.setCategoryId(4);
        service3.setPending(true);
        service3.setProviderID(3);
        service3.setName("Event Decoration");
        service3.setDescription("Professional event decoration for weddings and parties.");
        service3.setSpecification("Custom themes and high-quality materials.");
        service3.setPrice(3000);
        service3.setDiscount(5);
        service3.setPhotos(Arrays.asList("https://example.com/photos/decor1.jpg"));
        service3.setVisible(false);
        service3.setAvailable(true);
        service3.setMaxDuration(10);
        service3.setMinDuration(3);
        service3.setCancellationPeriod(72);
        service3.setReservationPeriod(96);
        service3.setAutoConfirm(false);
        services.add(service3);

        // Service 4
        GetServiceDTO service4 = new GetServiceDTO();
        service4.setId(4);
        service4.setCategoryId(2);
        service4.setPending(false);
        service4.setProviderID(4);
        service4.setName("Live Music Band");
        service4.setDescription("High-energy live music for all events.");
        service4.setSpecification("Band of 5 members with a mix of modern and classic hits.");
        service4.setPrice(7000);
        service4.setDiscount(15);
        service4.setPhotos(Arrays.asList("https://example.com/photos/band1.jpg", "https://example.com/photos/band2.jpg"));
        service4.setVisible(true);
        service4.setAvailable(false);
        service4.setMaxDuration(5);
        service4.setMinDuration(2);
        service4.setCancellationPeriod(48);
        service4.setReservationPeriod(60);
        service4.setAutoConfirm(true);
        services.add(service4);

        // Service 5
        GetServiceDTO service5 = new GetServiceDTO();
        service5.setId(5);
        service5.setCategoryId(5);
        service5.setPending(false);
        service5.setProviderID(5);
        service5.setName("Interactive DJ Service");
        service5.setDescription("Make your party unforgettable with our skilled DJ.");
        service5.setSpecification("Custom playlists and top-notch audio equipment.");
        service5.setPrice(2500);
        service5.setDiscount(20);
        service5.setPhotos(Arrays.asList("https://example.com/photos/dj1.jpg", "https://example.com/photos/dj2.jpg"));
        service5.setVisible(true);
        service5.setAvailable(true);
        service5.setMaxDuration(6);
        service5.setMinDuration(3);
        service5.setCancellationPeriod(36);
        service5.setReservationPeriod(48);
        service5.setAutoConfirm(false);
        services.add(service5);

        return new ResponseEntity<>(services, HttpStatus.OK);
    }
    
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GetServiceDTO> getBook(@PathVariable("id") int id) {
		GetServiceDTO service = new GetServiceDTO();

        service.setId(5);
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
	
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<GetServiceDTO>> getServicesByName(@RequestParam("name") String name) {
	    Collection<GetServiceDTO> services = new ArrayList<>();

	    // Example services for demonstration
	    GetServiceDTO service1 = new GetServiceDTO();
	    service1.setId(1);
	    service1.setName("Bridal Makeup");
	    service1.setDescription("Beautiful bridal makeup for the bride and her party.");
	    service1.setSpecification("We use the best products for long-lasting results.");
	    service1.setPrice(2000);
	    services.add(service1);

	    GetServiceDTO service2 = new GetServiceDTO();
	    service2.setId(2);
	    service2.setName("Wedding Photography");
	    service2.setDescription("Capture your special moments with our expert photographers.");
	    service2.setSpecification("Includes full-day coverage and edited photos.");
	    service2.setPrice(5000);
	    services.add(service2);

	    // Filter services based on the provided name
	    Collection<GetServiceDTO> filteredServices = new ArrayList<>();
	    for (GetServiceDTO service : services) {
	        if (service.getName().equalsIgnoreCase(name)) {
	            filteredServices.add(service);
	        }
	    }

	    if (filteredServices.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }

	    return new ResponseEntity<>(filteredServices, HttpStatus.OK);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CreatedServiceDTO> createBook(@RequestBody CreateServiceDTO service) throws Exception {
		CreatedServiceDTO createdService = new CreatedServiceDTO();

		createdService.setId(5);
		createdService.setCategoryId(5);
		createdService.setPending(false);
        createdService.setProviderID(5);
        createdService.setName("Interactive DJ Service");
        createdService.setDescription("Make your party unforgettable with our skilled DJ.");
        createdService.setSpecification("Custom playlists and top-notch audio equipment.");
        createdService.setPrice(2500);
        createdService.setDiscount(20);
        createdService.setPhotos(Arrays.asList("https://example.com/photos/dj1.jpg", "https://example.com/photos/dj2.jpg"));
        createdService.setVisible(true);
        createdService.setAvailable(true);
        createdService.setMaxDuration(6);
        createdService.setMinDuration(3);
        createdService.setCancellationPeriod(36);
        createdService.setReservationPeriod(48);
        createdService.setAutoConfirm(false);
		return new ResponseEntity<CreatedServiceDTO>(createdService, HttpStatus.CREATED);
	}
	
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UpdatedServiceDTO> updateBook(@RequestBody UpdateServiceDTO service, @PathVariable int id)
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
	public ResponseEntity<?> deleteGreeting(@PathVariable("id") int id) {
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
