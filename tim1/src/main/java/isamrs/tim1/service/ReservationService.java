package isamrs.tim1.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.FlightReservationDTO;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.UserReservation;
import isamrs.tim1.repository.FlightRepository;
import isamrs.tim1.repository.ReservationRepository;

@Service
public class ReservationService {
	
	@Autowired
	ReservationRepository reservationRepository;
	
	@Autowired
	FlightRepository flightRepository;
	
	public FlightReservationDTO reserveFlight(FlightReservationDTO flightRes) {
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Flight f = flightRepository.findOneByFlightCode(flightRes.getFlightCode());
		fr.setFlight(f);
		fr.setDone(false);
		fr.setDateOfReservation(new Date());
		return null;
	}

}
