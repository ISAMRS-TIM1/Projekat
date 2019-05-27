package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.FlightDTO;
import isamrs.tim1.dto.FlightUserViewDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.PlaneSeatsDTO;
import isamrs.tim1.service.FlightService;

@RestController
public class FlightController {
	
	@Autowired
	private FlightService flightService;
	
	@RequestMapping(value = "api/addFlight", method = RequestMethod.POST)
	public ResponseEntity<String> addFlight(@RequestBody FlightDTO flight) {
		return flightService.addFlight(flight);
	}
	
	@RequestMapping(value = "api/editFlight", method = RequestMethod.PUT)
	public ResponseEntity<MessageDTO> editFlight(@RequestBody FlightDTO flight) {
		return new ResponseEntity<MessageDTO>(flightService.editFlight(flight), HttpStatus.OK);
	}
	
	@RequestMapping(value = "api/searchFlights", method = RequestMethod.POST)
	public ArrayList<FlightUserViewDTO> searchFlights(@RequestBody FlightDTO flight) {
		return flightService.searchFlights(flight);
	}
	
	@RequestMapping(value = "/api/saveSeats", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> saveSeats(@RequestBody PlaneSeatsDTO seats) {
		return new ResponseEntity<MessageDTO>(flightService.saveSeats(seats), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getPlaneSeats", method = RequestMethod.GET)
	public ResponseEntity<PlaneSeatsDTO> getPlaneSeats(@RequestParam String flightCode) {
		return new ResponseEntity<PlaneSeatsDTO>(flightService.getPlaneSeats(flightCode), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/api/getDetailedFlight", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FlightDTO> getDetailedFlight(@RequestParam String flightCode) {
		return new ResponseEntity<FlightDTO>(flightService.getDetailedFlight(flightCode), HttpStatus.OK);
	}
}
