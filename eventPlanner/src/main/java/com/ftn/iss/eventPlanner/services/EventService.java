package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.PagedResponse;
import com.ftn.iss.eventPlanner.dto.event.CreateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.GetEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.dto.user.GetOrganizerDTO;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.Location;
import com.ftn.iss.eventPlanner.model.EventStats;
import com.ftn.iss.eventPlanner.model.specification.EventSpecification;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.EventStatsRepository;
import com.ftn.iss.eventPlanner.repositories.EventTypeRepository;
import com.ftn.iss.eventPlanner.repositories.OrganizerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LocationService locationService;
    @Autowired
    private EventTypeRepository eventTypeRepository;
    @Autowired
    private EventStatsRepository eventStatsRepository;
    @Autowired
    private OrganizerRepository organizerRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public List<GetEventDTO> findAll() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());
    }

    public List<GetEventDTO> getAllEvents(
            Integer eventTypeId,
            String location,
            Integer maxParticipants,
            Double minRating,
            String startDate,
            String endDate,
            String name
    ) {
        Specification<Event> specification = Specification.where(EventSpecification.hasEventTypeId(eventTypeId))
                .and(EventSpecification.hasLocation(location))
                .and(EventSpecification.maxParticipants(maxParticipants))
                .and(EventSpecification.betweenDates(startDate, endDate))
                .and(EventSpecification.hasName(name));

        List<Event> events = eventRepository.findAll(specification);

        return events.stream()
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());
    }


    public PagedResponse<GetEventDTO> getAllEvents(
            Pageable pageable,
            Integer eventTypeId,
            String location,
            Integer maxParticipants,
            Double minRating,
            String startDate,
            String endDate,
            String name,
            String sortBy,
            String sortDirection
    ) {
        if (sortBy != null && !"none".equalsIgnoreCase(sortBy)) {
            String sortField = switch (sortBy.toLowerCase()) {
                case "name" -> "name";
                case "date" -> "date";
                case "averagerating" -> "stats.averageRating";
                case "location.city" -> "location.city";
                default -> null;
            };

            if (sortField != null) {
                var sortDirectionEnum = "desc".equalsIgnoreCase(sortDirection)
                        ? org.springframework.data.domain.Sort.Direction.DESC
                        : org.springframework.data.domain.Sort.Direction.ASC;

                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        org.springframework.data.domain.Sort.by(sortDirectionEnum, sortField));
            }
        }

        Specification<Event> specification = Specification.where(EventSpecification.hasEventTypeId(eventTypeId))
                .and(EventSpecification.hasLocation(location))
                .and(EventSpecification.maxParticipants(maxParticipants))
                .and(EventSpecification.betweenDates(startDate, endDate))
                .and(EventSpecification.minAverageRating(minRating))
                .and(EventSpecification.hasName(name));

        Page<Event> pagedEvents = eventRepository.findAll(specification, pageable);

        List<GetEventDTO> eventDTOs = pagedEvents.getContent().stream()
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());

        return new PagedResponse<>(eventDTOs, pagedEvents.getTotalPages(), pagedEvents.getTotalElements());
    }





    public List<GetEventDTO> findTopEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .sorted((e1, e2) -> e2.getDateCreated().compareTo(e1.getDateCreated()))
                .limit(5)
                .map(this::mapToGetEventDTO)
                .collect(Collectors.toList());
    }

    public CreatedEventDTO create (CreateEventDTO createEventDTO){
        Event event = modelMapper.map(createEventDTO, Event.class);
        event.setId(0);
        Location location = modelMapper.map(locationService.create(createEventDTO.getLocation()), Location.class);
        event.setLocation(location);
        EventType eventType = null;
        if(createEventDTO.getEventTypeId()!=0) {
            eventType=eventTypeRepository.findById(createEventDTO.getEventTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + createEventDTO.getEventTypeId() + " not found"));
        }
        event.setEventType(eventType);
        event.setStats(eventStatsRepository.save(new EventStats()));
        event.setOrganizer(organizerRepository.findById(createEventDTO.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer with ID " + createEventDTO.getOrganizerId() + " not found")));
        event.setDeleted(false);
        event.setDateCreated(java.time.LocalDateTime.now());
        event = eventRepository.save(event);
        eventRepository.flush();
        return modelMapper.map(event, CreatedEventDTO.class);
    }


    // HELPER FUNCTIONS

    private GetEventDTO mapToGetEventDTO(Event event) {
        GetEventDTO dto = new GetEventDTO();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setOrganizer(setGetOrganizerDTO(event));
        dto.setEventType(modelMapper.map(event.getEventType(), GetEventTypeDTO.class));

        if (event.getLocation() != null) {
            GetLocationDTO locationDTO = setGetLocationDTO(event);
            dto.setLocation(locationDTO);
        }

        dto.setMaxParticipants(event.getMaxParticipants());
        dto.setAverageRating(event.getStats().getAverageRating());
        dto.setDescription(event.getDescription());
        dto.setOpen(event.isOpen());
        return dto;
    }

    private GetLocationDTO setGetLocationDTO (Event event){
        GetLocationDTO locationDTO = new GetLocationDTO();
        locationDTO.setCountry(event.getLocation().getCountry());
        locationDTO.setCity(event.getLocation().getCity());
        locationDTO.setStreet(event.getLocation().getStreet());
        locationDTO.setHouseNumber(event.getLocation().getHouseNumber());
        return locationDTO;
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


    private Comparator<Event> getEventComparator(String sortBy, String sortDirection) {
        if (sortBy == null || "none".equalsIgnoreCase(sortBy)) {
            return null;
        }

        Comparator<Event> comparator = switch (sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(Event::getName, String.CASE_INSENSITIVE_ORDER);
            case "date" -> Comparator.comparing(Event::getDate);
            case "averagerating" -> Comparator.comparing(event -> event.getStats() != null
                    ? event.getStats().getAverageRating() : 0.0);
            case "location.city" -> Comparator.comparing(event -> event.getLocation().getCity(), String.CASE_INSENSITIVE_ORDER);
            default -> null;
        };

        if (comparator != null && "desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }


}
