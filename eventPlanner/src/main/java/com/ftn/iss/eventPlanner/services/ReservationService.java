package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.model.Reservation;
import com.ftn.iss.eventPlanner.model.ServiceDetails;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
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
    private DTOMapper dtoMapper;

    public List<GetReservationDTO> findAll(){
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(dtoMapper::mapToGetReservationDTO).toList();
    }

    public GetReservationDTO findById(int id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Reservation with ID " + id + " not found"));
        return dtoMapper.mapToGetReservationDTO(reservation);
    }

    public List<GetReservationDTO> findByServiceId(int serviceId) {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .filter(reservation -> reservation.getService().getId() == serviceId)
                .map(dtoMapper::mapToGetReservationDTO)
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

        return dtoMapper.mapServiceDetailsDTO(service, serviceDetails);
    }

    public List<GetReservationDTO> findByOrganizerId(int organizerId) {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .filter(reservation -> reservation.getEvent().getOrganizer().getId() == organizerId)
                .map(dtoMapper::mapToGetReservationDTO)
                .toList();
    }

    public List<GetReservationDTO> findByProviderId(int providerId) {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .filter(reservation -> reservation.getService().getProvider().getId() == providerId)
                .map(dtoMapper::mapToGetReservationDTO)
                .toList();
    }

}
