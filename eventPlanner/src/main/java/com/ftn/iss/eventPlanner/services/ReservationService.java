package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.EventPlannerApplication;
import com.ftn.iss.eventPlanner.dto.company.GetCompanyDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.reservation.CreateReservationDTO;
import com.ftn.iss.eventPlanner.dto.reservation.CreatedReservationDTO;
import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.dto.user.GetOrganizerDTO;
import com.ftn.iss.eventPlanner.dto.user.GetProviderDTO;
import com.ftn.iss.eventPlanner.model.*;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
import com.ftn.iss.eventPlanner.repositories.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EventPlannerApplication eventPlannerApplication;

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

    public ServiceDetails findServiceDetailsByReservationId(int id){
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

        return serviceDetails;
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

    private void isDateWithinReservationPeriod(LocalTime startTime, Event event, ServiceDetails serviceDetails) {
        LocalDate eventDate = event.getDate();
        LocalDateTime reservationStart = LocalDateTime.of(eventDate, startTime);

        long requiredHoursInAdvance = serviceDetails.getReservationPeriod();
        LocalDateTime latestAllowedReservationTime = reservationStart.minusHours(requiredHoursInAdvance);

        if(LocalDateTime.now().isAfter(latestAllowedReservationTime)){
            throw new IllegalArgumentException("Reservation must be made within the reservation period.");
        };
    }

    private void isDateWithinCancellationPeriod(Event event, ServiceDetails serviceDetails){
        LocalDate eventDate = event.getDate();

        int requiredDaysInAdvance = serviceDetails.getCancellationPeriod();

        if(LocalDateTime.now().isAfter(eventDate.minusDays(requiredDaysInAdvance).atStartOfDay())){
            throw new IllegalArgumentException("Cancellation must be made within the reservation period.");
        }
    }
    public void validateReservationTime(LocalTime startTime, LocalTime endTime, ServiceDetails serviceDetails) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time must be provided");
        }

        long selectedDuration = Duration.between(startTime, endTime).toMinutes();

        if (selectedDuration <= 0) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        long minDurationInMinutes = serviceDetails.getMinDuration() * 60;
        long maxDurationInMinutes = serviceDetails.getMaxDuration() * 60;

        if (selectedDuration < minDurationInMinutes || selectedDuration > maxDurationInMinutes) {
            throw new IllegalArgumentException("Selected duration is not within the service's duration limits");
        }
    }

    private void checkServiceAvailability(LocalDate date, LocalTime startTime, LocalTime endTime, com.ftn.iss.eventPlanner.model.Service service) {
        List<Reservation> allReservations = reservationRepository.findAll();

        List<Reservation> relevantReservations = allReservations.stream()
                .filter(reservation -> reservation.getService().getId() == service.getId() && reservation.getEvent().getDate().equals(date))
                .collect(Collectors.toList());

        LocalDateTime providedStart = LocalDateTime.of(date, startTime);
        LocalDateTime providedEnd = LocalDateTime.of(date, endTime);

        for (Reservation reservation : relevantReservations) {
            LocalDateTime reservationStartDateTime = LocalDateTime.of(date,  reservation.getStartTime());
            LocalDateTime reservationEndDateTime = LocalDateTime.of(date, reservation.getEndTime());

            if ((providedStart.isBefore(reservationEndDateTime) && providedStart.isAfter(reservationStartDateTime)) ||
                    (providedEnd.isBefore(reservationEndDateTime) && providedEnd.isAfter(reservationStartDateTime)) ||
                    (providedStart.isEqual(reservationStartDateTime) || providedEnd.isEqual(reservationEndDateTime))) {
                throw new IllegalArgumentException("Service not available at selected time.");
            }
        }
    }
    private void isServiceReservedForEvent(Event event, com.ftn.iss.eventPlanner.model.Service service) {
        List<Reservation> allReservations = reservationRepository.findAll();

        for (Reservation reservation : allReservations) {
            if (reservation.getService().getId() == service.getId() && reservation.getEvent().getId() == event.getId()) {
                throw new IllegalArgumentException("You've already made a reservation for selected event.");
            }
        }
    }

    public CreatedReservationDTO create(CreateReservationDTO reservation) {
        Reservation createdReservation = new Reservation();
        Event event = eventRepository.findById(reservation.getEvent())
                .orElseThrow(() -> new NotFoundException("Event with ID " + reservation.getEvent() + " not found"));
        com.ftn.iss.eventPlanner.model.Service service = serviceRepository.findById(reservation.getService())
                .orElseThrow(()-> new NotFoundException("Service with ID " + reservation.getService() + "not found"));

        LocalTime startTime = reservation.getStartTime();
        LocalTime endTime = reservation.getEndTime();

        isServiceReservedForEvent(event, service);
        isDateWithinReservationPeriod(startTime, event, service.getCurrentDetails());
        validateReservationTime(startTime, endTime,service.getCurrentDetails());
        checkServiceAvailability(event.getDate(), startTime, endTime, service);

        createdReservation.setService(service);
        createdReservation.setEvent(event);
        createdReservation.setStartTime(startTime);
        createdReservation.setEndTime(endTime);
        createdReservation.setTimestamp(LocalDateTime.now());
        if(service.getCurrentDetails().isAutoConfirm()){
            createdReservation.setStatus(Status.ACCEPTED);
        }else{
            createdReservation.setStatus(Status.PENDING);
        }

        createdReservation = reservationRepository.save(createdReservation);
        reservationRepository.flush();

        CreatedReservationDTO createdReservationDTO = modelMapper.map(createdReservation, CreatedReservationDTO.class);
        createdReservationDTO.setServiceId(createdReservation.getService().getId());
        createdReservationDTO.setEventId(createdReservation.getEvent().getId());

        sendConfirmation(event, service);

        return createdReservationDTO;
    }
    private void sendConfirmation(Event event, com.ftn.iss.eventPlanner.model.Service service) {
        EmailDetails emailDetails=new EmailDetails();
        emailDetails.setRecipient(event.getOrganizer().getAccount().getEmail());
        emailDetails.setSubject("Reservation Confirmation");
        emailDetails.setMsgBody("You've successfully reserved "+service.getCurrentDetails().getName()+" for "+event.getName()+"!");
        emailService.sendSimpleEmail(emailDetails);

        emailDetails=new EmailDetails();
        emailDetails.setRecipient(service.getProvider().getAccount().getEmail());
        emailDetails.setSubject("Your Service Has Gotten A Reservation");
        if (service.getCurrentDetails().isAutoConfirm()){
            emailDetails.setMsgBody("Your service "+service.getCurrentDetails().getName()+" has been reserved for "+event.getName()+" by "+event.getOrganizer().getFirstName()+" "+event.getOrganizer().getLastName()+" and has been automatically accepted.");
        }else{
            emailDetails.setMsgBody("Your service "+service.getCurrentDetails().getName()+" has been reserved for "+event.getName()+" by "+event.getOrganizer().getFirstName()+" "+event.getOrganizer().getLastName()+" and has been added to pending reservation where you can confirm/deny it.");
        }
        emailService.sendSimpleEmail(emailDetails);
    }

    public List<GetEventDTO> findEventsByOrganizer(Integer accountId) {
        List<Event> events = eventRepository.findAll();
        List<GetEventDTO> eventDTOs = new ArrayList<>();
        if (accountId != null) {
            eventDTOs = events.stream()
                    .filter(event -> event.getOrganizer().getAccount().getId() == accountId)
                    .filter(event -> !event.getDate().isBefore(LocalDate.now()))
                    .map(this::mapToGetEventDTO)
                    .collect(Collectors.toList());
        }
        return eventDTOs;
    }

    public void cancelReservation(int id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with ID " + id + " not found"));
        ServiceDetails reservationServiceDetails = findServiceDetailsByReservationId(id);

        isDateWithinCancellationPeriod(reservation.getEvent(), reservationServiceDetails);
        reservation.setStatus(Status.CANCELED);
        reservationRepository.save(reservation);
    }

    public void acceptReservation(int reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation with ID " + reservationId + " not found"));
        reservation.setStatus(Status.ACCEPTED);
        reservationRepository.save(reservation);
    }

    public void rejectReservation(int reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation with ID " + reservationId + " not found"));
        reservation.setStatus(Status.DENIED);
        reservationRepository.save(reservation);
    }
}
