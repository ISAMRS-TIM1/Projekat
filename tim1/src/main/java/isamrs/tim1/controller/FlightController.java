package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.FlightDTO;
import isamrs.tim1.dto.FlightUserViewDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.service.FlightService;

@RestController
public class FlightController {
	
	@Autowired
	private FlightService flightService;
	
	@RequestMapping(value = "api/addFlight", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> addFlight(@RequestBody FlightDTO flight) {
		return flightService.addFlight(flight);
	}
	
	@RequestMapping(value = "api/searchFlights", method = RequestMethod.POST)
	public ArrayList<FlightUserViewDTO> searchFlights(@RequestBody FlightDTO flight) {
		return flightService.searchFlights(flight);
	}
}
