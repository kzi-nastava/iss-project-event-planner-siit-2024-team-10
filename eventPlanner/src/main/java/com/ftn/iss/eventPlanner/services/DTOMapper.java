package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offering.GetOfferingDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.dto.user.GetOrganizerDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class DTOMapper {

    private final ModelMapper modelMapper;
    @Autowired
    public DTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public GetReservationDTO mapToGetReservationDTO(Reservation reservation){
        GetReservationDTO getReservationDTO = new GetReservationDTO();

        getReservationDTO.setId(reservation.getId());
        getReservationDTO.setService(mapToGetServiceDTO(reservation.getService()));
        getReservationDTO.setEvent(mapToGetEventDTO(reservation.getEvent()));
        getReservationDTO.setStartTime(reservation.getStartTime());
        getReservationDTO.setEndTime(reservation.getEndTime());
        getReservationDTO.setStatus(reservation.getStatus());

        return getReservationDTO;
    }

    public  GetEventDTO mapToGetEventDTO(Event event) {
        GetEventDTO dto = new GetEventDTO();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setOrganizer(setGetOrganizerDTO(event));
        if(event.getEventType()!=null)
            dto.setEventType(modelMapper.map(event.getEventType(), GetEventTypeDTO.class));

        if (event.getLocation() != null) {
            GetLocationDTO locationDTO = setGetLocationDTO(event);
            dto.setLocation(locationDTO);
        }

        dto.setMaxParticipants(event.getMaxParticipants());
        if (event.getStats()!=null) {
            dto.setAverageRating(event.getStats().getAverageRating());
        }else{
            dto.setAverageRating(0);
        }
        dto.setDescription(event.getDescription());
        dto.setOpen(event.isOpen());
        return dto;
    }

    public  GetLocationDTO setGetLocationDTO (Event event){
        GetLocationDTO locationDTO = new GetLocationDTO();
        locationDTO.setCountry(event.getLocation().getCountry());
        locationDTO.setCity(event.getLocation().getCity());
        locationDTO.setStreet(event.getLocation().getStreet());
        locationDTO.setHouseNumber(event.getLocation().getHouseNumber());
        return locationDTO;
    }

    public  GetOrganizerDTO setGetOrganizerDTO(Event event){
        GetOrganizerDTO organizerDTO = new GetOrganizerDTO();
        organizerDTO.setId(event.getOrganizer().getId());
        organizerDTO.setEmail(event.getOrganizer().getAccount().getEmail());
        organizerDTO.setFirstName(event.getOrganizer().getFirstName());
        organizerDTO.setLastName(event.getOrganizer().getLastName());
        organizerDTO.setPhoneNumber(event.getOrganizer().getPhoneNumber());
        organizerDTO.setProfilePhoto(event.getOrganizer().getProfilePhoto());
        organizerDTO.setLocation(modelMapper.map(event.getOrganizer().getLocation(), GetLocationDTO.class));
        return organizerDTO;
    }

    public  GetServiceDTO mapToGetServiceDTO(com.ftn.iss.eventPlanner.model.Service service) {
        GetServiceDTO dto = new GetServiceDTO();

        dto.setId(service.getId());
        dto.setCategory(modelMapper.map(service.getCategory(), GetOfferingCategoryDTO.class));
        dto.setPending(service.isPending());
        dto.setProvider(setGetProviderDTO(service));
        dto.setName(service.getCurrentDetails().getName());
        dto.setDescription(service.getCurrentDetails().getDescription());
        dto.setSpecification(service.getCurrentDetails().getSpecification());
        dto.setPrice(service.getCurrentDetails().getPrice());
        dto.setDiscount(service.getCurrentDetails().getDiscount());
        dto.setPhotos(service.getCurrentDetails().getPhotos());
        dto.setAvailable(service.getCurrentDetails().isAvailable());
        dto.setDeleted(service.isDeleted());
        dto.setVisible(service.getCurrentDetails().isVisible());
        dto.setMaxDuration(service.getCurrentDetails().getMaxDuration());
        dto.setMinDuration(service.getCurrentDetails().getMinDuration());
        dto.setCancellationPeriod(service.getCurrentDetails().getCancellationPeriod());
        dto.setReservationPeriod(service.getCurrentDetails().getReservationPeriod());
        dto.setAutoConfirm(service.getCurrentDetails().isAutoConfirm());
        return dto;
    }
    public  GetProviderDTO setGetProviderDTO(Offering offering){
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
    public  GetCompanyDTO setGetCompanyDTO(Offering offering){
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

    public GetOfferingDTO mapToGetOfferingDTO(Offering offering) {
        GetOfferingDTO dto = new GetOfferingDTO();

        dto.setId(offering.getId());
        dto.setProvider(setGetProviderDTO(offering));
        dto.setCategory(modelMapper.map(offering.getCategory(), GetOfferingCategoryDTO.class));
        dto.setAverageRating(calculateAverageRating(offering));
        if (offering.getClass().equals(Product.class)) {
            Product pr = (Product) offering;
            dto.setName(pr.getCurrentDetails().getName());
            dto.setDescription(pr.getCurrentDetails().getDescription());
            dto.setPrice(pr.getCurrentDetails().getPrice());
            dto.setDiscount(pr.getCurrentDetails().getDiscount());
            dto.setLocation(modelMapper.map(pr.getProvider().getLocation(), GetLocationDTO.class));

            dto.setProduct(true);
        }
        else{
            Service service = (Service) offering;
            dto.setName(service.getCurrentDetails().getName());
            dto.setDescription(service.getCurrentDetails().getDescription());
            dto.setPrice(service.getCurrentDetails().getPrice());
            dto.setDiscount(service.getCurrentDetails().getDiscount());
            dto.setLocation(modelMapper.map(service.getProvider().getLocation(), GetLocationDTO.class));
            dto.setSpecification(service.getCurrentDetails().getSpecification());

            dto.setProduct(false);
        }
        return dto;
    }

    public double calculateAverageRating(Offering offering) {
        if (offering.getRatings() == null || offering.getRatings().isEmpty()) {
            return 0.0;
        }
        OptionalDouble average = offering.getRatings().stream()
                .mapToInt(Rating::getScore)
                .average();

        return average.orElse(0.0);
    }

    public GetServiceDTO mapServiceDetailsDTO(Service service, ServiceDetails serviceDetails) {
        GetServiceDTO serviceDTO = new GetServiceDTO();

        serviceDTO.setName(serviceDetails.getName());
        serviceDTO.setDescription(serviceDetails.getDescription());
        serviceDTO.setSpecification(serviceDetails.getSpecification());
        serviceDTO.setPrice(serviceDetails.getPrice());
        serviceDTO.setDiscount(serviceDetails.getDiscount());
        serviceDTO.setPhotos(serviceDetails.getPhotos());
        serviceDTO.setVisible(serviceDetails.isVisible());
        serviceDTO.setAvailable(serviceDetails.isAvailable());
        serviceDTO.setMaxDuration(serviceDetails.getMaxDuration());
        serviceDTO.setMinDuration(serviceDetails.getMinDuration());
        serviceDTO.setCancellationPeriod(serviceDetails.getCancellationPeriod());
        serviceDTO.setReservationPeriod(serviceDetails.getReservationPeriod());
        serviceDTO.setAutoConfirm(serviceDetails.isAutoConfirm());

        serviceDTO.setId(service.getId());
        serviceDTO.setPending(service.isPending());
        serviceDTO.setDeleted(service.isDeleted());
        serviceDTO.setCategory(modelMapper.map(service.getCategory(), GetOfferingCategoryDTO.class));
        serviceDTO.setProvider(setGetProviderDTO(service));

        return serviceDTO;
    }


}
