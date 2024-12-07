package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.event.CreateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import com.ftn.iss.eventPlanner.repositories.EventStatsRepository;
import com.ftn.iss.eventPlanner.repositories.EventTypeRepository;
import com.ftn.iss.eventPlanner.repositories.OrganizerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<GetEventCardDTO> findAll() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToGetEventCardDTO)
                .collect(Collectors.toList());
    }

    public List<GetEventCardDTO> findTopEvents(){
        List<Event> events = eventRepository.findAll();

        // sorting by timestamp, DESC - TBD when timestamp attribute gets added
        return events.stream()
                .map(this::mapToGetEventCardDTO)
                .limit(5)
                .collect(Collectors.toList());
    }

    private GetEventCardDTO mapToGetEventCardDTO(Event event) {
        GetEventCardDTO dto = new GetEventCardDTO();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setOrganizer(event.getOrganizer().getFirstName()+" "+event.getOrganizer().getLastName());
        dto.setEventType(event.getEventType().getName());

        if (event.getLocation() != null) {
            LocationDTO locationDTO = setLocationDTO(event);
            dto.setLocation(locationDTO);
        }

        if (event.getRatings() != null && !event.getRatings().isEmpty()) {
            double averageRating = event.getRatings()
                    .stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0);
            dto.setAverageRating(averageRating);
        } else {
            dto.setAverageRating(0.0);
        }

    public CreatedEventDTO create(CreateEventDTO createEventDTO) {
        Event event = modelMapper.map(createEventDTO, Event.class);
        event.setId(0);
        Location location = modelMapper.map(locationService.create(createEventDTO.getLocation()), Location.class);
        event.setLocation(location);
        event.setEventType(eventTypeRepository.findById(createEventDTO.getEventTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Event Type with ID " + createEventDTO.getEventTypeId() + " not found")));
        event.setStats(eventStatsRepository.save(new EventStats()));
        event.setOrganizer(organizerRepository.findById(createEventDTO.getOrganizerId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer with ID " + createEventDTO.getOrganizerId() + " not found")));
        event.setDeleted(false);
        event.setDateCreated(java.time.LocalDate.now());
        event = eventRepository.save(event);
        eventRepository.flush();
        return modelMapper.map(event, CreatedEventDTO.class);
    }

    private LocationDTO setLocationDTO(Event event){
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setCountry(event.getLocation().getCountry());
        locationDTO.setCity(event.getLocation().getCity());
        locationDTO.setStreet(event.getLocation().getStreet());
        locationDTO.setHouseNumber(event.getLocation().getHouseNumber());
        return locationDTO;
    }

}
