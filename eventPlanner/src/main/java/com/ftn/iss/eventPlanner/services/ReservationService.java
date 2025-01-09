package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.event.GetEventDTO;
import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import com.ftn.iss.eventPlanner.model.Reservation;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private GetReservationDTO mapToGetReservationDTO(Reservation reservation){
        GetReservationDTO getReservationDTO = new GetReservationDTO();

        getReservationDTO.setId(reservation.getId());
        getReservationDTO.setService(modelMapper.map(reservation.getService(), GetServiceDTO.class));
        getReservationDTO.setEvent(modelMapper.map(reservation.getEvent(), GetEventDTO.class));
        getReservationDTO.setStartTime(reservation.getStartTime());
        getReservationDTO.setEndTime(reservation.getEndTime());
        getReservationDTO.setStatus(reservation.getStatus());

        return getReservationDTO;
    }
}
