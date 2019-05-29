package isamrs.tim1.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.repository.AirlineRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class PeriodicCheckService {
	
	@Autowired
	AirlineRepository airlineRepository;
	
	void checkReservations() {
		Date now = new Date();
		int numOfDays;
		
		for(Airline airline : airlineRepository.findAll()) {
			for(FlightReservation fr : airline.getReservations()) {
				numOfDays = (int)( (now.getTime() - fr.getFlight().getDepartureTime().getTime()) / (1000 * 60 * 60 * 24)) + 1;
				if(numOfDays <= 3) {
					fr.setDone(true);
				}			}
		}
	}

}
