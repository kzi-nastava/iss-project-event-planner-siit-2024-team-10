package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.offering.GetOfferingCardDTO;
import com.ftn.iss.eventPlanner.model.Offering;
import com.ftn.iss.eventPlanner.model.Product;
import com.ftn.iss.eventPlanner.repositories.OfferingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.ftn.iss.eventPlanner.model.Service;
import com.ftn.iss.eventPlanner.model.Rating;


import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class OfferingService {
    @Autowired
    private OfferingRepository offeringRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public List<GetOfferingCardDTO> findAll(){
        List<Offering> offerings = offeringRepository.findAll();

        return offerings.stream()
                .map(this::mapToGetOfferingCardDTO)
                .collect(Collectors.toList());
    }




    private GetOfferingCardDTO mapToGetOfferingCardDTO(Offering offering) {
        GetOfferingCardDTO dto = new GetOfferingCardDTO();
        dto.setId(offering.getId());
        dto.setName(offering.getProvider().getFirstName()+" "+offering.getProvider().getLastName());
        dto.setCategory(offering.getCategory().getName());
        dto.setAverageRating(calculateAverageRating(offering));

        if (offering.getClass().equals(Product.class)) {
            Product pr = (Product) offering;
            dto.setName(pr.getCurrentDetails().getName());
            dto.setPrice(pr.getCurrentDetails().getPrice());

            // TO BE CHANGED WHEN PHOTOS ATTRIBUTE IS CHANGED TO A SET
            Set<String> photos = pr.getCurrentDetails().getPhotos();
            if (photos != null && !photos.isEmpty()) {
                String coverPicture = new ArrayList<>(photos).getFirst();
                dto.setCoverPicture(coverPicture);
            }
            dto.setIsService(false);
        }
        else{
            Service service = (Service) offering;
            dto.setName(service.getCurrentDetails().getName());
            dto.setPrice(service.getCurrentDetails().getPrice());
            List<String> photos = service.getCurrentDetails().getPhotos();
            if (photos != null && !photos.isEmpty()) {
                String coverPicture = new ArrayList<>(photos).getFirst();
                dto.setCoverPicture(coverPicture);
            }
            dto.setIsService(true);
        }
        return dto;
    }

    private double calculateAverageRating(Offering offering) {
        if (offering.getRatings() == null || offering.getRatings().isEmpty()) {
            return 0.0;
        }
        OptionalDouble average = offering.getRatings().stream()
                .mapToInt(Rating::getScore)
                .average();

        return average.orElse(0.0);
    }
}
