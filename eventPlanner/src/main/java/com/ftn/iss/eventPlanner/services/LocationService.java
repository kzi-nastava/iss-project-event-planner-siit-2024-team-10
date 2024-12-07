package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.eventtype.CreateEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.eventtype.CreatedEventTypeDTO;
import com.ftn.iss.eventPlanner.dto.location.CreateLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.CreatedLocationDTO;
import com.ftn.iss.eventPlanner.dto.location.GetLocationDTO;
import com.ftn.iss.eventPlanner.model.EventType;
import com.ftn.iss.eventPlanner.model.Location;
import com.ftn.iss.eventPlanner.model.OfferingCategory;
import com.ftn.iss.eventPlanner.repositories.LocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    ModelMapper modelMapper = new ModelMapper();

    //gets location from database if it exists, otherwise creates a new location
    public CreatedLocationDTO create(CreateLocationDTO locationDTO){
        Location location = locationRepository.findByAllFields(locationDTO.getCountry(), locationDTO.getCity(), locationDTO.getStreet(), locationDTO.getHouseNumber())
                .orElseGet(() -> null);
        if(location == null){
            location = modelMapper.map(locationDTO, Location.class);
            location = locationRepository.save(location);
        }
        return modelMapper.map(location, CreatedLocationDTO.class);
    }

    public GetLocationDTO findById(int id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location with ID " + id + " not found"));
        return modelMapper.map(location, GetLocationDTO.class);
    }

    public List<GetLocationDTO> findAll() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(location -> modelMapper.map(location, GetLocationDTO.class))
                .collect(Collectors.toList());
    }
}
