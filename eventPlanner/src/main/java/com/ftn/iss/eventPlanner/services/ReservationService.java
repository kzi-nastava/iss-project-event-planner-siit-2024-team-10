package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.dto.user.GetOrganizerDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.Reservation;
import com.ftn.iss.eventPlanner.model.ServiceDetails;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<GetReservationDTO> findAll(){
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::mapToGetReservationDTO).toList();
    }

    public GetReservationDTO findById(int id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Reservation with ID " + id + " not found"));
        return mapToGetReservationDTO(reservation);
    }

    public List<GetReservationDTO> findByServiceId(int serviceId) {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .filter(reservation -> reservation.getService().getId() == serviceId)
                .map(this::mapToGetReservationDTO)
                .toList();
    }

    public GetServiceDTO findServiceDetailsByReservationId(int id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Reservation with ID " + id + " not found"));

        com.ftn.iss.eventPlanner.model.Service service = reservation.getService();
        LocalDateTime timestamp = reservation.getTimestamp();
        ServiceDetails serviceDetails;

        if (service.getServiceDetailsHistory().isEmpty()){
            serviceDetails = service.getCurrentDetails();
        }else{
            serviceDetails = service.getServiceDetailsHistory().stream()
                    .filter(details -> details.getTimestamp().isBefore(timestamp))
                    .max(Comparator.comparing(ServiceDetails::getTimestamp))
                    .orElse(service.getCurrentDetails());
        }

        return mapServiceDetailsDTO(service, serviceDetails);
    }

    public List<GetReservationDTO> findByOrganizerId(int organizerId) {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .filter(reservation -> reservation.getEvent().getOrganizer().getId() == organizerId)
                .map(this::mapToGetReservationDTO)
                .toList();
    }

    public List<GetReservationDTO> findByProviderId(int providerId) {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .filter(reservation -> reservation.getService().getProvider().getId() == providerId)
                .map(this::mapToGetReservationDTO)
                .toList();
    }

    private GetReservationDTO mapToGetReservationDTO(Reservation reservation){
        GetReservationDTO getReservationDTO = new GetReservationDTO();

        getReservationDTO.setId(reservation.getId());
        getReservationDTO.setService(mapToGetServiceDTO(reservation.getService()));
        getReservationDTO.setEvent(mapToGetEventDTO(reservation.getEvent()));
        getReservationDTO.setStartTime(reservation.getStartTime());
        getReservationDTO.setEndTime(reservation.getEndTime());
        getReservationDTO.setStatus(reservation.getStatus());

        return getReservationDTO;
    }
    private GetEventDTO mapToGetEventDTO(Event event) {
        GetEventDTO dto = new GetEventDTO();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setOrganizer(setGetOrganizerDTO(event));
        if(event.getEventType()!=null)
            dto.setEventType(modelMapper.map(event.getEventType(), GetEventTypeDTO.class));

        if (event.getLocation() != null) {
            dto.setLocation(modelMapper.map(event.getLocation(), GetLocationDTO.class));
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

    private GetOrganizerDTO setGetOrganizerDTO(Event event){
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

    private  GetServiceDTO mapToGetServiceDTO(com.ftn.iss.eventPlanner.model.Service service) {
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
    public GetProviderDTO setGetProviderDTO(Offering offering){
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
    public GetCompanyDTO setGetCompanyDTO(Offering offering){
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

    public GetServiceDTO mapServiceDetailsDTO(com.ftn.iss.eventPlanner.model.Service service, ServiceDetails serviceDetails) {
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
