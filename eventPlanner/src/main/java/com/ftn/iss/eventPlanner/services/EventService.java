package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.LocationDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventCardDTO;
import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.model.Event;
import com.ftn.iss.eventPlanner.model.Rating;
import com.ftn.iss.eventPlanner.repositories.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    private ModelMapper modelMapper = new ModelMapper();

    public List<GetEventCardDTO> findAll() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::mapToGetEventCardDTO)
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

        return dto;
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
