package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.FlightHotelReservationDTO;
import isamrs.tim1.dto.FlightReservationDTO;
import isamrs.tim1.dto.FlightVehicleReservationDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.service.ReservationService;

@RestController
public class ReservationController {

	@Autowired
	ReservationService reservationService;

	@RequestMapping(value = "/api/reserveFlight", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> reserveFlight(@RequestBody FlightReservationDTO flightRes) {
		return new ResponseEntity<MessageDTO>(reservationService.reserveFlight(flightRes), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/reserveFlightHotel", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> reserveFlightHotel(@RequestBody FlightHotelReservationDTO flightHotelRes) {
		return new ResponseEntity<MessageDTO>(reservationService.reserveFlightHotel(flightHotelRes), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/reserveFlightVehicle", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> reserveFlightVehicle(@RequestBody FlightVehicleReservationDTO flightVehicleRes) {
		return new ResponseEntity<MessageDTO>(reservationService.reserveFlightVehicle(flightVehicleRes), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getReservations", method = RequestMethod.GET)
	public ArrayList<FlightReservationDTO> getReservations() {
		return reservationService.getReservations();
	}
}
