package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.event.CreateEventDTO;
import com.ftn.iss.eventPlanner.dto.event.CreatedEventDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventCardDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.model.Location;
import com.ftn.iss.eventPlanner.model.EventStats;
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

    public List<GetEventCardDTO> findTopEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .sorted((e1, e2) -> e2.getDateCreated().compareTo(e1.getDateCreated()))
                .limit(5)
                .map(this::mapToGetEventCardDTO)
                .collect(Collectors.toList());
    }

    private GetEventCardDTO mapToGetEventCardDTO(Event event) {
        GetEventCardDTO dto = new GetEventCardDTO();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setOrganizer(event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName());
        dto.setEventType(event.getEventType().getName());

        if (event.getLocation() != null) {
            GetLocationDTO locationDTO = setGetLocationDTO(event);
            dto.setLocation(locationDTO);
        }

        dto.setAverageRating(calculateAverageRating(event));
        return dto;
    }


    public CreatedEventDTO create (CreateEventDTO createEventDTO){
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

    private GetLocationDTO setGetLocationDTO (Event event){
        GetLocationDTO locationDTO = new GetLocationDTO();
        locationDTO.setCountry(event.getLocation().getCountry());
        locationDTO.setCity(event.getLocation().getCity());
        locationDTO.setStreet(event.getLocation().getStreet());
        locationDTO.setHouseNumber(event.getLocation().getHouseNumber());
        return locationDTO;
    }

    private double calculateAverageRating(Event event){
        if (event.getStats() != null) {
            EventStats eventStats = event.getStats();
            double averageRating = (double) (eventStats.getFiveStarCount() +
                    eventStats.getFourStarCount() +
                    eventStats.getThreeStarCount() +
                    eventStats.getTwoStarCount() +
                    eventStats.getOneStarCount()) /
                    eventStats.getParticipantsCount();
            return averageRating;
        } else {
            return 0.0;
        }
    }

}
