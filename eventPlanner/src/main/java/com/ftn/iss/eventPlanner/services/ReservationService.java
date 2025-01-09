package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.reservation.GetReservationDTO;
import com.ftn.iss.eventPlanner.model.Reservation;
import com.ftn.iss.eventPlanner.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
